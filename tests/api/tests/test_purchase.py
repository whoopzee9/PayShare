import random
import uuid

import pytest_check as check
from loguru import logger

from tests.api.markers import *


# -------------- REQUESTS TO TEST ----------------

# post /user/room/{room_id}/purchase только участники комнаты
# put /user/room/{room_id}/purchase/{purchase_id} только создатель покупки
# delete /user/room/{room_id}/purchase/{purchase_id} только создатель покупки


class TestPurchase:

    # Добавление покупки
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
        res = api_svc.add_purchase(room_id=room_id, name=new_purchase_name, shop="shop", cost=new_purchase_cost)
        id = res["id"]
        room_after = api_svc.get_room(room_id)
        purchases_after = room_after["room_info"]["purchases"]
        check.equal(len(purchases) + 1, len(purchases_after))
        new_purchase = purchases_after[-1]
        check.equal(new_purchase["id"], id)
        check.equal(new_purchase["owner_id"], your_id)
        check.equal(new_purchase["name"], new_purchase_name)
        check.equal(new_purchase["cost"], new_purchase_cost)
        #check.equal(new_purchase["participants"], new_purchase_cost)

    # Невозможно добавить покупки в закрытую комнату
    def test_add_purchase_to_closed_room(self, thread_user_google):
        _, api_svc = thread_user_google
        closed_rooms = api_svc.get_closed_rooms()["rooms"]
        closed_rooms_count = len(closed_rooms)
        closed_ids = [elem["room"]["id"] for elem in closed_rooms] if closed_rooms_count != 0 else []
        room_id = random.choice(closed_ids)
        room = api_svc.get_room(room_id)
        logger.info(f"{room=}")
        your_id = room["your_participant_id"]
        purchases = room["room_info"]["purchases"]
        new_purchase_name = f"test-purchase{uuid.uuid1()}"
        new_purchase_cost = 150
        try:
            res = api_svc.add_purchase(room_id=room_id, name=new_purchase_name, shop="shop", cost=new_purchase_cost)
        except Exception as e:
            logger.info(f"{e=}")
        room_after = api_svc.get_room(room_id)
        purchases_after = room_after["room_info"]["purchases"]
        check.equal(len(purchases), len(purchases_after))

    # Присоединение к покупке
    def test_mark_purchase(self, thread_user_google):
        _, api_svc = thread_user_google
        assert 1

    # Отметка долга
    def test_mark_debt(self, thread_user_google):
        _, api_svc = thread_user_google
        assert 1

    # Изменение покупки
    def test_edit_purchase(self, thread_user_vk):
        _, api_svc = thread_user_vk
        assert 1

    # Удаление покупки
    def test_delete_purchase(self, thread_user_vk):
        _, api_svc = thread_user_vk
        assert 1


