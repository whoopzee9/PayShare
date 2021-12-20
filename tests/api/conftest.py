import json

import pytest
import requests

from loguru import logger
from tests.api.constants import BASE_URL


@pytest.fixture(scope="session")
def thread_user_google():
    url_auth = BASE_URL + "/auth/login"

    data = {"auth_api": "google"}
    res = requests.post(url_auth, data=json.dumps(data))
    assert res.ok
    res_data = res.json()
    logger.debug(res_data)
    return res_data["access_token"]


@pytest.fixture(scope="session")
def thread_user_vk():
    url_auth = BASE_URL + "/auth/login"

    data = {"auth_api": "vk"}
    res = requests.post(url_auth, data=json.dumps(data))
    assert res.ok
    res_data = res.json()
    logger.debug(res_data)
    return res_data["access_token"]
