import requests
from loguru import logger

from tests.api.constants import BASE_URL
from tests.api.markers import *


# -------------- REQUESTS TO TEST ----------------

# post /user/room/{room_id}/purchase только участники комнаты
# put /user/room/{room_id}/purchase/{purchase_id} только создатель покупки
# delete /user/room/{room_id}/purchase/{purchase_id} только создатель покупки


class TestPurchase:

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_get_purchases(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user/room/{room_id}"
        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.get(url, headers=head)
        assert res.ok
        res_data = res.json()
        logger.debug(res_data)

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_add_new_purchase(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user/room/{room_id}/purchase"
        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.post(url, headers=head)
        assert res.ok
        res_data = res.json()
        logger.debug(res_data)

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_edit_purchase(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user/room/{room_id}/purchase/{purchase_id}"
        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.put(url, params={"help": "true"}, headers=head)
        logger.debug(res.json())

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_delete_purchase(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user/room/{room_id}/purchase/{purchase_id}"
        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.put(url, params={"help": "true"}, headers=head)
        logger.debug(res.json())
