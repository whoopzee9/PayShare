import datetime
import json

import requests

from tests.api.constants import BASE_URL
from loguru import logger


class APISvc:
    _endpoints = {
        "login": "/auth/login",
        "refresh_token": "/auth/refresh",
        "logout": "/auth/logout",
        "user": "/user",
        "opened_rooms": "/user/room/opened",
        "closed_rooms": "/user/room/closed",
        "room": "/user/room",
        "join_room": "/user/room/join",
        "close_room": "/user/room/{room_id}/close",
        "invite_code": "/user/room/{room_id}/code",
        "leave_room": "/user/room/{room_id}/leave_room",
        "kick_room_participant": "/user/room/{room_id}/participant/{participant_id}",
        "purchase": "/user/room/{room_id}/purchase",
        "join_purchase": "/user/room/{room_id}}/purchase/{purchase_id}/join",
        "check_purchase_paid": "/user/room/{room_id}}/purchase/{purchase_id}/paid",

    }

    def __init__(self):
        self.endpoint = BASE_URL
        self.token = self.login()

    def login(self, params=None, data=None, auth_type="google"):
        if data is None:
            data = {}
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["login"]
        assert auth_type.lower() in ("vk", "google")

        data["auth_api"] = auth_type.lower()
        res = requests.post(url, params=params, data=json.dumps(data))
        return res.json()["access_token"]

    def refresh_token(self, params=None, data=None):
        if data is None:
            data = {}
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["refresh_token"]
        res = requests.post(url, params=params, data=json.dumps(data))
        token = res.json()["access_token"]
        self.token = token
        return token

    def get_user(self, params=None):
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["user"]
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.get(url, headers=head, params=params)
        assert res.ok
        res_data = res.json()
        return res_data

    def create_room(self, room_name, room_date=datetime.date.today(), params=None):
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["room"]
        data = {"room_name": room_name, "room_date": room_date}
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.post(url, headers=head, params=params, data=json.dumps(data))
        assert res.ok
        res_data = res.json()
        return res_data

    def get_room_by_code(self, room_code, params=None):
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["join_room"]
        data = {"code": str(room_code)}
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.post(url, headers=head, params=params, data=json.dumps(data))
        assert res.ok, res.text
        logger.debug(res.text)
        res_data = res.json() if res.text is not "" else None
        return res_data

    def join_room_by_id(self, room_id, params=None, data=None):
        if data is None:
            data = {}
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["join_room"] + f"/{room_id}"
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.post(url, headers=head, params=params, data=json.dumps(data))
        logger.debug(res)
        assert res.ok, res.text
        res_data = res.json() if res.text is not "" else None
        return res_data

    def close_room(self, room_id, params=None, data=None):
        if data is None:
            data = {}
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["close_room"].replace("{room_id}", str(room_id))
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.put(url, headers=head, params=params, data=json.dumps(data))
        assert res.ok, res.text
        res_data = res.json() if res.text is not "" else None
        return res_data

    def leave_room(self, room_id, params=None, data=None):
        if data is None:
            data = {}
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["leave_room"].replace("{room_id}", str(room_id))
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.delete(url, headers=head, params=params, data=json.dumps(data))
        assert res.ok, res.text
        res_data = res.json() if res.text is not "" else None
        return res_data

    def kick_user_from_room(self, room_id, participant_id, params=None, data=None):
        if data is None:
            data = {}
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["leave_room"].replace("{room_id}", str(room_id)).replace(
            "{participant_id}", participant_id)
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.delete(url, headers=head, params=params, data=json.dumps(data))
        assert res.ok, res.text
        res_data = res.json() if res.text is not "" else None
        return res_data

    def delete_room(self, room_id, params=None):
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["room"] + f"/{room_id}"
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.delete(url, headers=head, params=params)
        assert res.ok, res.text
        res_data = res.json() if res.text is not "" else None
        return res_data

    def get_room(self, room_id, params=None):
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["room"] + f"/{room_id}"
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.get(url, headers=head, params=params)
        assert res.ok, res.text
        res_data = res.json()
        return res_data

    def get_opened_rooms(self, params=None):
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["opened_rooms"]
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.get(url, headers=head, params=params)
        assert res.ok
        res_data = res.json()
        return res_data

    def get_closed_rooms(self, params=None):
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["closed_rooms"]
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.get(url, headers=head, params=params)
        assert res.ok
        res_data = res.json()
        return res_data

    def add_purchase(self, room_id, params=None, data=None):
        if params is None:
            params = {}
        if data is None:
            data = {}
        url = self.endpoint + self._endpoints["purchase"].replace("{room_id}", str(room_id))
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.post(url, headers=head, params=params, data=json.dumps(data))
        assert res.ok
        res_data = res.json()
        return res_data

    def get_invite_code(self, room_id, params=None, data=None):
        if data is None:
            data = {}
        if params is None:
            params = {}
        url = self.endpoint + self._endpoints["invite_code"].replace("{room_id}", str(room_id))
        head = {"Accept": "application/json", "Authorization": f"Bearer {self.token}"}
        res = requests.post(url, headers=head, params=params, data=json.dumps(data))
        assert res.ok, res.text
        res_data = res.json()
        return res_data
