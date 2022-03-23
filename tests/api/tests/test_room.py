import datetime
import random

import pytest
import pytest_check as check
from loguru import logger

from tests.api.markers import *


# -------------- REQUESTS TO TEST ----------------

# post /user/room любой пользователь
# post /user/room/join любой пользователь
# put /user/room/{room_id}/close только создатель комнаты
# delete /user/room/{room_id} только создатель комнаты
# post /user/room/{room_id}/code только участники комнаты
# delete /user/room/{room_id}/participant/{participant_id} только создатель комнаты


class TestRoom:

    @pytest.mark.parametrize("day", ["today", -1, 1])
    def test_create_room(self, thread_user_google, day):
        token, api_svc = thread_user_google
        time = datetime.date.today()
        if day != "today":
            time += datetime.timedelta(days=day)

        room_res = api_svc.create_room(room_name=f"test-room{datetime.datetime.now()}", room_date=str(time))

        room_id = room_res["id"]
        logger.debug(f"{room_id=}")
        check.is_true(room_id)

        get_room_res = api_svc.get_room(room_id)

        check.equal(get_room_res["your_participant_id"], get_room_res["room_info"]["owner_participant_id"])
        check.equal(len(get_room_res["room_info"]["participants"]), 1)
        check.equal(len(get_room_res["room_info"]["purchases"]), 0)

        opened_rooms = api_svc.get_opened_rooms()["rooms"]

        room = [elem for elem in opened_rooms if elem["room"]["id"] == room_id]
        assert len(room) == 1
        room = room[0]
        check.equal(room["is_your"], True)
        check.equal(room["room"]["id"], room_id)
        check.equal(room["room"]["id"], room_id)
        check.equal(room["room"]["close"], False)
        check.equal(room["room"]["room_date"], str(time))
        check.is_in(f"test-room", room["room"]["room_name"])
        check.is_none(room["purchases"])

    def test_check_opened_and_closed_rooms_after_create(self, thread_user_google):
        token, api_svc = thread_user_google
        opened_rooms = api_svc.get_opened_rooms()["rooms"]
        opened_before_count = len(opened_rooms) if opened_rooms is not None else 0
        closed_rooms = api_svc.get_closed_rooms()["rooms"]
        closed_before_count = len(closed_rooms) if closed_rooms is not None else 0
        ids = []
        for day in range(-2, 3):
            time = datetime.date.today() + datetime.timedelta(days=day)
            room_res = api_svc.create_room(room_name=f"test-room{datetime.datetime.now()}", room_date=str(time))
            ids.append(room_res["id"])

        opened_rooms = api_svc.get_opened_rooms()["rooms"]
        opened_after_count = len(opened_rooms) if opened_rooms is not None else 0
        opened_ids = [elem["room"]["id"] for elem in opened_rooms] if opened_after_count != 0 else []

        closed_rooms = api_svc.get_closed_rooms()["rooms"]
        closed_after_count = len(closed_rooms) if closed_rooms is not None else 0
        closed_ids = [elem["room"]["id"] for elem in closed_rooms] if closed_after_count != 0 else []

        check.equal(opened_after_count, opened_before_count + len(ids))
        check.equal(closed_after_count, closed_before_count)
        check.is_true(all(id in opened_ids for id in ids))
        check.is_true(all(id not in closed_ids for id in ids), msg=f"{closed_ids=}, {ids=}")

    def test_join_code_info(self, thread_user_google, thread_user_vk):
        _, api_svc_google = thread_user_google
        token, api_svc_vk = thread_user_vk
        time = datetime.date.today()
        room_res = api_svc_vk.create_room(room_name=f"test-room{datetime.datetime.now()}", room_date=str(time))
        room_id = room_res["id"]

        room_data = api_svc_vk.get_room(room_id)
        invite_code = api_svc_vk.get_invite_code(room_id)["code"]
        logger.debug(f"{invite_code=}")
        res = api_svc_google.get_room_by_code(invite_code)

        check.equal(res["room"]["room"]["id"], room_id)
        check.equal(res["room"]["room"]["room_date"], str(time))
        check.equal(res["room"]["room"]["close"], False)
        check.equal(res["room"]["is_your"], False)
        purchases = res["room"]["purchases"] if res["room"]["purchases"] is not None else []
        check.equal(purchases, room_data["room_info"]["purchases"])

    @pytest.mark.parametrize("day", ["today", -1, 1])
    def test_join_room(self, thread_user_google, thread_user_vk, day):
        token_vk, api_svc_vk = thread_user_vk
        token_google, api_svc_google = thread_user_google
        time = datetime.date.today()
        if day != "today":
            time += datetime.timedelta(days=day)

        room_res = api_svc_vk.create_room(room_name=f"test-room{datetime.datetime.now()}", room_date=str(time))
        room_id = room_res["id"]

        res = api_svc_vk.get_invite_code(room_id)
        invite_code = res["code"]
        logger.debug(f"{invite_code=}")
        res = api_svc_vk.get_invite_code(room_id)
        invite_code = res["code"]
        logger.debug(f"{invite_code=}")
        assert invite_code
        res = api_svc_google.get_room_by_code(invite_code)
        logger.debug(res)
        res = api_svc_google.join_room_by_id(room_id)
        logger.debug(res)

        get_room_res = api_svc_google.get_room(room_id)
        logger.debug(f"{get_room_res=}")
        check.not_equal(get_room_res["your_participant_id"], get_room_res["room_info"]["owner_participant_id"])
        check.equal(len(get_room_res["room_info"]["participants"]), 2)
        check.equal(len(get_room_res["room_info"]["purchases"]), 0)

    @pytest.mark.xfail(reason="Negative test case")
    @pytest.mark.parametrize("group_type", ["owner", "user"])
    def test_cant_join_room_twice(self, thread_user_google, thread_user_vk, group_type):
        token_vk, api_svc_vk = thread_user_vk
        token_google, api_svc_google = thread_user_google
        time = datetime.date.today()
        room_res = api_svc_vk.create_room(room_name=f"test-room{datetime.datetime.now()}", room_date=str(time))
        room_id = room_res["id"]
        # res = api_svc_vk.get_invite_code(room_id)
        # invite_code = res["code"]
        if group_type == "owner":
            api_svc_vk.join_room_by_id(room_id)
        else:
            try:
                res = api_svc_google.join_room_by_id(room_id)
            except Exception as e:
                logger.debug("Error")
                return
            try:
                res = api_svc_google.join_room_by_id(room_id)
            except Exception as e:
                pytest.fail(e.args)

    def test_check_opened_and_closed_rooms_after_join(self, thread_user_google, thread_user_vk):
        _, api_svc_vk = thread_user_vk
        _, api_svc_google = thread_user_google
        opened_rooms = api_svc_google.get_opened_rooms()["rooms"]
        opened_before_count = len(opened_rooms) if opened_rooms is not None else 0
        closed_rooms = api_svc_google.get_closed_rooms()["rooms"]
        closed_before_count = len(closed_rooms) if closed_rooms is not None else 0
        ids = []

        for day in range(-2, 3):
            time = datetime.date.today() + datetime.timedelta(days=day)
            room_res = api_svc_vk.create_room(room_name=f"test-room{datetime.datetime.now()}", room_date=str(time))
            ids.append(room_res["id"])
        for room in ids:
            res = api_svc_google.join_room_by_id(room)

        opened_rooms = api_svc_google.get_opened_rooms()["rooms"]
        opened_after_count = len(opened_rooms) if opened_rooms is not None else 0
        opened_ids = [elem["room"]["id"] for elem in opened_rooms] if opened_after_count != 0 else []

        closed_rooms = api_svc_google.get_closed_rooms()["rooms"]
        closed_after_count = len(closed_rooms) if closed_rooms is not None else 0
        closed_ids = [elem["room"]["id"] for elem in closed_rooms] if closed_after_count != 0 else []

        check.equal(opened_after_count, opened_before_count + len(ids))
        check.equal(closed_after_count, closed_before_count)
        check.is_true(all(id in opened_ids for id in ids))
        check.is_true(all(id not in closed_ids for id in ids), msg=f"{closed_ids=}, {ids=}")

    def test_close_room(self, thread_user_vk):
        _, api_svc = thread_user_vk

        opened_rooms = api_svc.get_opened_rooms()["rooms"]
        opened_before_count = len(opened_rooms) if opened_rooms is not None else 0
        opened_ids = [elem["room"]["id"] for elem in opened_rooms if elem["is_your"] is True] if opened_before_count != 0 else []

        closed_rooms = api_svc.get_closed_rooms()["rooms"]
        closed_before_count = len(closed_rooms) if closed_rooms is not None else 0

        room_id = random.choice(opened_ids)
        api_svc.close_room(room_id)
        opened_rooms = api_svc.get_opened_rooms()["rooms"]
        logger.info(f"{opened_rooms=}")
        opened_after_count = len(opened_rooms) if opened_rooms is not None else 0
        opened_ids = [elem["room"]["id"] for elem in opened_rooms] if opened_after_count != 0 else []

        closed_rooms = api_svc.get_closed_rooms()["rooms"]
        logger.info(f"{closed_rooms=}")
        closed_after_count = len(closed_rooms) if closed_rooms is not None else 0
        closed_ids = [elem["room"]["id"] for elem in closed_rooms] if closed_after_count != 0 else []
        check.equal(opened_after_count, opened_before_count - 1)
        check.is_not_in(room_id, opened_ids)
        check.equal(closed_after_count, closed_before_count + 1)
        check.is_in(room_id, closed_ids)

    def test_reopen_closed_room(self, thread_user_vk):
        _, api_svc = thread_user_vk

        opened_rooms = api_svc.get_opened_rooms()["rooms"]
        opened_before_count = len(opened_rooms) if opened_rooms is not None else 0
        closed_rooms = api_svc.get_closed_rooms()["rooms"]
        closed_before_count = len(closed_rooms) if closed_rooms is not None else 0
        closed_ids = [elem["room"]["id"] for elem in closed_rooms if
                      elem["is_your"] is True] if closed_before_count != 0 else []

        room_id = random.choice(closed_ids)
        api_svc.close_room(room_id)
        opened_rooms = api_svc.get_opened_rooms()["rooms"]
        logger.info(f"{opened_rooms=}")
        opened_after_count = len(opened_rooms) if opened_rooms is not None else 0
        opened_ids = [elem["room"]["id"] for elem in opened_rooms] if opened_after_count != 0 else []

        closed_rooms = api_svc.get_closed_rooms()["rooms"]
        logger.info(f"{closed_rooms=}")
        closed_after_count = len(closed_rooms) if closed_rooms is not None else 0
        closed_ids = [elem["room"]["id"] for elem in closed_rooms] if closed_after_count != 0 else []

        check.equal(opened_after_count, opened_before_count + 1)
        check.is_in(room_id, opened_ids)
        check.equal(closed_after_count, closed_before_count - 1)
        check.is_not_in(room_id, closed_ids)
        api_svc.close_room(room_id)

    @pytest.mark.parametrize("room", ["closed", "opened"])
    def test_delete_room(self, thread_user_vk, room):
        _, api_svc = thread_user_vk
        opened_rooms = api_svc.get_opened_rooms()["rooms"]
        opened_before_count = len(opened_rooms) if opened_rooms is not None else 0

        closed_rooms = api_svc.get_closed_rooms()["rooms"]
        closed_before_count = len(closed_rooms) if closed_rooms is not None else 0
        if room == "opened":
            opened_ids = [elem["room"]["id"] for elem in opened_rooms if
                          elem["is_your"] is True] if opened_before_count != 0 else []
            room_id = random.choice(opened_ids)
        else:
            closed_ids = [elem["room"]["id"] for elem in closed_rooms if
                          elem["is_your"] is True] if closed_before_count != 0 else []
            room_id = random.choice(closed_ids)

        res = api_svc.delete_room(room_id)
        logger.debug(res)

        opened_rooms = api_svc.get_opened_rooms()["rooms"]
        opened_after_count = len(opened_rooms) if opened_rooms is not None else 0
        opened_ids = [elem["room"]["id"] for elem in opened_rooms] if opened_after_count != 0 else []

        closed_rooms = api_svc.get_closed_rooms()["rooms"]
        closed_after_count = len(closed_rooms) if closed_rooms is not None else 0
        closed_ids = [elem["room"]["id"] for elem in closed_rooms] if closed_after_count != 0 else []

        check.is_not_in(room_id, opened_ids)
        check.is_not_in(room_id, closed_ids)
        if room == "opened":
            check.equal(opened_after_count, opened_before_count - 1)
            check.equal(closed_after_count, closed_before_count)
        else:
            check.equal(opened_after_count, opened_before_count)
            check.equal(closed_after_count, closed_before_count - 1)

    @pytest.mark.xfail(reason="Negative test case")
    @pytest.mark.parametrize("type", ["code", "join"])
    def test_cant_join_closed_room(self, thread_user_google, type):
        _, api_svc = thread_user_google
        closed_rooms = api_svc.get_closed_rooms()["rooms"]
        closed_ids = [elem["room"]["id"] for elem in closed_rooms] if len(closed_rooms) != 0 else []

        room_id = random.choice(closed_ids)
        if type == "code":
            api_svc.get_invite_code(room_id)
        else:
            api_svc.join_room_by_id(room_id)

    @pytest.mark.parametrize("room_type", ["open", "closed"])
    def test_leave_room_user(self, thread_user_google, thread_user_vk, room_type):
        _, api_svc_google = thread_user_google
        _, api_svc_vk = thread_user_vk
        opened_rooms_user = api_svc_google.get_opened_rooms()["rooms"]
        opened_before_count_user = len(opened_rooms_user) if opened_rooms_user is not None else 0
        opened_ids = [elem["room"]["id"] for elem in opened_rooms_user if
                      elem["is_your"] is False] if opened_before_count_user != 0 else []

        opened_rooms_owner = api_svc_vk.get_opened_rooms()["rooms"]
        opened_before_count_owner = len(opened_rooms_owner) if opened_rooms_owner is not None else 0

        closed_rooms = api_svc_google.get_closed_rooms()["rooms"]
        closed_before_count = len(closed_rooms) if closed_rooms is not None else 0
        if room_type == "open":
            room_id = random.choice(opened_ids)
        else:
            closed_ids = [elem["room"]["id"] for elem in closed_rooms if
                          elem["is_your"] is False] if closed_before_count != 0 else []
            room_id = random.choice(closed_ids)

        room_info_before = api_svc_google.get_room(room_id)
        if room_type == "closed":
            with pytest.raises(Exception) as e:
                res = api_svc_google.leave_room(room_id)
            logger.error(e)
            return
        res = api_svc_google.leave_room(room_id)
        logger.info(f"{res}")
        room_info_after = api_svc_vk.get_room(room_id)

        opened_rooms_user = api_svc_google.get_opened_rooms()["rooms"]
        opened_after_count_user = len(opened_rooms_user) if opened_rooms_user is not None else 0
        opened_ids = [elem["room"]["id"] for elem in opened_rooms_user] if opened_after_count_user != 0 else []

        opened_rooms_owner = api_svc_vk.get_opened_rooms()["rooms"]
        opened_after_count_owner = len(opened_rooms_owner) if opened_rooms_owner is not None else 0

        closed_rooms = api_svc_google.get_closed_rooms()["rooms"]
        closed_after_count = len(closed_rooms) if closed_rooms is not None else 0
        closed_ids = [elem["room"]["id"] for elem in closed_rooms] if closed_after_count != 0 else []

        check.equal(opened_after_count_user, opened_before_count_user - 1)
        check.is_not_in(room_id, opened_ids)
        check.equal(closed_after_count, closed_before_count)
        check.is_not_in(room_id, closed_ids)
        check.is_true(room_info_after != room_info_before)
        check.equal(opened_before_count_owner, opened_after_count_owner)
        check.equal(len(room_info_after["room_info"]["participants"]), len(room_info_before["room_info"]["participants"]) - 1)

    @pytest.mark.parametrize("room_type", ["open", "closed"])
    @pytest.mark.xfail(reason="Negative test case")
    def test_leave_room_owner(self, thread_user_vk, room_type):
        _, api_svc = thread_user_vk
        opened_rooms_user = api_svc.get_opened_rooms()["rooms"]
        opened_before_count_user = len(opened_rooms_user) if opened_rooms_user is not None else 0
        opened_ids = [elem["room"]["id"] for elem in opened_rooms_user if
                      elem["is_your"] is True] if opened_before_count_user != 0 else []
        closed_rooms = api_svc.get_closed_rooms()["rooms"]
        closed_before_count = len(closed_rooms) if closed_rooms is not None else 0
        if room_type == "open":
            room_id = random.choice(opened_ids)
        else:
            closed_ids = [elem["room"]["id"] for elem in closed_rooms if
                          elem["is_your"] is True] if closed_before_count != 0 else []
            room_id = random.choice(closed_ids)

        room_info_before = api_svc.get_room(room_id)
        res = api_svc.leave_room(room_id)
        logger.debug(f"Test passed: {res}")

    def test_room_operation_by_user(self, thread_user_google):
        _, api_svc = thread_user_google
        opened_rooms_user = api_svc.get_opened_rooms()["rooms"]
        opened_before_count_user = len(opened_rooms_user) if opened_rooms_user is not None else 0
        closed_rooms_user = api_svc.get_closed_rooms()["rooms"]
        closed_before_count_user = len(closed_rooms_user) if closed_rooms_user is not None else 0
        opened_ids = [elem["room"]["id"] for elem in opened_rooms_user if
                      elem["is_your"] is False] if opened_before_count_user != 0 else []
        room_id = random.choice(opened_ids)
        with pytest.raises(Exception) as e1:
            res = api_svc.close_room(room_id)
            logger.debug(res)
        logger.info(f"close - {e1.value}")
        with pytest.raises(Exception) as e3:
            res = api_svc.delete_room(room_id)
            logger.debug(res)
        logger.info(f"delete - {e3.value}")

        opened_rooms_user = api_svc.get_opened_rooms()["rooms"]
        opened_after_count_user = len(opened_rooms_user) if opened_rooms_user is not None else 0
        closed_rooms_user = api_svc.get_closed_rooms()["rooms"]
        closed_after_count_user = len(closed_rooms_user) if closed_rooms_user is not None else 0
        check.equal(closed_after_count_user, closed_before_count_user)
        check.equal(opened_after_count_user, opened_before_count_user)

