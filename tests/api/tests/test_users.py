import pytest
import requests
from tests.api.markers import *

from loguru import logger
from tests.api.constants import BASE_URL

# -------------- REQUESTS TO TEST ----------------

# get /user
# put /user

class TestUsers:

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_get_user(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user"

        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.get(url, headers=head)
        assert res.ok
        res_data = res.json()
        logger.debug(res_data)
        assert all(item in res_data.keys() for item in ["first_name", "second_name", "image_url"])

    @pytest.mark.skip(reason="Useless")
    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_put_user(self, thread_user_google, thread_user_vk, auth_type):
        url = BASE_URL + "/user"
        head = {}
        head["Accept"] = "application/json"
        head["Authorization"] = f"Bearer {thread_user_google}" if auth_type == "google" else f"Bearer {thread_user_vk}"
        res = requests.put(url, params={"help": "true"}, headers=head)
        logger.debug(res.json())
