import datetime

import pytest
import requests
from loguru import logger
import pytest_check as check

from tests.api.constants import BASE_URL
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

        print(get_room_res)
        check.equal(get_room_res["your_participant_id"], get_room_res["room_info"]["owner_participant_id"])
        check.equal(len(get_room_res["room_info"]["participants"]), 1)
        check.equal(len(get_room_res["room_info"]["purchases"]), 0)
        #
        # get_room_res = api_svc.get_opened(params="id=7")

    def test_check_opened_rooms_after_create(self, thread_user_google):
        token, api_svc = thread_user_google
        rooms = api_svc.get_opened()["rooms"]
        opened_before_count = len(rooms)
        ids = []
        for day in range(-2, 3):
            time = datetime.date.today() + datetime.timedelta(days=day)
            room_res = api_svc.create_room(room_name=f"test-room{datetime.datetime.now()}", room_date=str(time))
            ids.append(room_res["id"])
        rooms = api_svc.get_opened()["rooms"]
        opened_after_count = len(rooms)
        assert opened_after_count == opened_before_count + len(ids)

    @th_current
    @pytest.mark.parametrize("join_type", ["code", "id"])
    @pytest.mark.parametrize("day", ["today", -1, 1])
    def test_join_room(self, thread_user_google, thread_user_vk, join_type, day):
        token_vk, api_svc_vk = thread_user_vk
        token_google, api_svc_google = thread_user_google
        time = datetime.date.today()
        if day != "today":
            time += datetime.timedelta(days=day)

        room_res = api_svc_vk.create_room(room_name=f"test-room{datetime.datetime.now()}", room_date=str(time))
        room_id = room_res["id"]
        if join_type == "code":
            res = api_svc_vk.get_invite_code(room_id)
            invite_code = res["code"]
            assert invite_code
            res = api_svc_google.join_room_by_code(invite_code)
            logger.debug(res)
        else:
            res = api_svc_google.join_room_by_id(room_id)
            logger.debug(res)

    @pytest.mark.xfail(reason="Negative test case")
    @pytest.mark.parametrize("group_type", ["owner", "user"])
    def test_join_room_negative(self, thread_user_google, thread_user_vk, join_type, day):
        token_vk, api_svc_vk = thread_user_vk
        token_google, api_svc_google = thread_user_google
        time = datetime.date.today()
        if day != "today":
            time += datetime.timedelta(days=day)

        room_res = api_svc_vk.create_room(room_name=f"test-room{datetime.datetime.now()}", room_date=str(time))
        room_id = room_res["id"]
        if join_type == "code":
            res = api_svc_vk.get_invite_code(room_id)
            invite_code = res["code"]
            assert invite_code
            res = api_svc_google.join_room_by_code(invite_code)
            logger.debug(res)
        else:
            res = api_svc_google.join_room_by_id(room_id)
            logger.debug(res)







    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_get_opened_rooms(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user/room/opened"  # todo: add upper

        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.get(url, headers=head)
        assert res.ok
        res_data = res.json()
        logger.debug(res_data)

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_get_closed_rooms(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user/room/closed"  # todo: add upper

        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.get(url, headers=head)
        assert res.ok
        res_data = res.json()
        logger.debug(res_data)

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

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_close_room(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user/room/{room_id}/close"
        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.put(url, params={"help": "true"}, headers=head)
        logger.debug(res.json())

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_delete_room(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user/room/{room_id}"
        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.put(url, params={"help": "true"}, headers=head)
        logger.debug(res.json())

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_get_group_invite_code(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user/room/{room_id}/code"
        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.post(url, params={"help": "true"}, headers=head)
        logger.debug(res.json())

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_get_group_invite_code(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user/room/{room_id}/participant/{participant_id}"
        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.delete(url, params={"help": "true"}, headers=head)
        logger.debug(res.json())

    def test_join_room_by_code(self, thread_user_google):
        token, api_svc = thread_user_google
        res = api_svc.get_room(room_id=1)
        api_svc.join_room(room_id=1)

    def test_join_room_by_id(self, thread_user_google):
        token, api_svc = thread_user_google
        res = api_svc.get_room(room_id=1)
        api_svc.join_room(room_id=1)
