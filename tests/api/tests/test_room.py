import requests
from tests.api.markers import *

from loguru import logger
from tests.api.constants import BASE_URL


# -------------- REQUESTS TO TEST ----------------

# post /user/room любой пользователь
# post /user/room/join любой пользователь
# put /user/room/{room_id}/close только создатель комнаты
# delete /user/room/{room_id} только создатель комнаты
# post /user/room/{room_id}/code только участники комнаты
# delete /user/room/{room_id}/participant/{participant_id} только создатель комнаты


class TestRoom:

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

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_create_room(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user/room"

        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.get(url, headers=head)
        assert res.ok
        res_data = res.json()
        logger.debug(res_data)

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_join_room(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user/room/join"
        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.post(url, params={"help": "true"}, headers=head)
        logger.debug(res.json())

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
