import random
import uuid

import requests
import pytest_check as check
from loguru import logger

from tests.api.constants import BASE_URL
from tests.api.markers import *


# -------------- REQUESTS TO TEST ----------------

# post /user/room/{room_id}/purchase только участники комнаты
# put /user/room/{room_id}/purchase/{purchase_id} только создатель покупки
# delete /user/room/{room_id}/purchase/{purchase_id} только создатель покупки


class TestPurchase:

    def test_add_purchase(self, thread_user_google):
        _, api_svc = thread_user_google
        opened_rooms = api_svc.get_opened_rooms()["rooms"]
        opened_rooms_count = len(opened_rooms)
        opened_ids = [elem["room"]["id"] for elem in opened_rooms] if opened_rooms_count != 0 else []
        room_id = random.choice(opened_ids)
        room = api_svc.get_room(room_id)
        logger.info(f"{room=}")
        your_id = room["your_participant_id"]
        purchases = room["room_info"]["purchases"]
        new_purchase_name = f"test-purchase{uuid.uuid1()}"
        new_purchase_cost = 150
        res = api_svc.add_purchase(room_id=room_id,  data={"id": 1, "owner_id": your_id, "name": new_purchase_name, "locate": "shop", "cost": new_purchase_cost})
        logger.debug(res)
        room_after = api_svc.get_room(room_id)
        purchases_after = room_after["room_info"]["purchases"]
        check.equal(len(purchases) + 1, len(purchases_after))


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
