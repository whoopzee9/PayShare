import datetime
import random

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

@th_current
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
    def test_join_room_negative(self, thread_user_google, thread_user_vk, group_type):
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

    def test_close_room(self, thread_user_google):
        _, api_svc = thread_user_google

        opened_rooms = api_svc.get_opened_rooms()["rooms"]
        opened_after_count = len(opened_rooms) if opened_rooms is not None else 0
        opened_ids = [elem["room"]["id"] for elem in opened_rooms] if opened_after_count != 0 else []
        room_id = random.choice(opened_ids)
        api_svc.close_room(room_id)
    #     head = {}
    #     head["Accept"] = "application/json"
    #     head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
    #     res = requests.put(url, params={"help": "true"}, headers=head)
    #     logger.debug(res.json())
    #

    def test_delete_room(self, thread_user_google):
        _, api_svc = thread_user_google

        opened_rooms = api_svc.get_opened_rooms()["rooms"]
        opened_after_count = len(opened_rooms) if opened_rooms is not None else 0
        opened_ids = [elem["room"]["id"] for elem in opened_rooms] if opened_after_count != 0 else []
        room_id = random.choice(opened_ids)
        api_svc.delete_room(room_id)
    #     url = BASE_URL + "/user/room/{room_id}"
    #     head = {}
    #     head["Accept"] = "application/json"
    #     head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
    #     res = requests.put(url, params={"help": "true"}, headers=head)
    #     logger.debug(res.json())


    # @pytest.mark.parametrize("auth_type", ["vk", "google"])
    # def test_create_room(self, thread_user_google, thread_user_vk, auth_type):
    #     url = BASE_URL + "/user/room"
    #
    #     head = {}
    #     head["Accept"] = "application/json"
    #     head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
    #     res = requests.get(url, headers=head)
    #     assert res.ok
    #     res_data = res.json()
    #     logger.debug(res_data)

    # @pytest.mark.parametrize("auth_type", ["vk", "google"])
    # def test_join_room(self, thread_user_google, thread_user_vk, auth_type):
    #     url = BASE_URL + "/user/room/join"
    #     head = {}
    #     head["Accept"] = "application/json"
    #     head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
    #     res = requests.post(url, params={"help": "true"}, headers=head)
    #     logger.debug(res.json())

    # @pytest.mark.parametrize("auth_type", ["vk", "google"])
    # def test_close_room(self, thread_user_google, thread_user_vk, auth_type):
    #     url = BASE_URL + "/user/room/{room_id}/close"
    #     head = {}
    #     head["Accept"] = "application/json"
    #     head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
    #     res = requests.put(url, params={"help": "true"}, headers=head)
    #     logger.debug(res.json())
    #
    # @pytest.mark.parametrize("auth_type", ["vk", "google"])
    # def test_delete_room(self, thread_user_google, thread_user_vk, auth_type):
    #     url = BASE_URL + "/user/room/{room_id}"
    #     head = {}
    #     head["Accept"] = "application/json"
    #     head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
    #     res = requests.put(url, params={"help": "true"}, headers=head)
    #     logger.debug(res.json())
