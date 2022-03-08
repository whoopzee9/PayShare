import json

import pytest
import requests

from loguru import logger

from tests.api.APISvc import APISvc
from tests.api.constants import BASE_URL


@pytest.fixture(scope="session")
def thread_user_google():
    api_svc = APISvc()
    token = api_svc.login(auth_type="google")
    return token, api_svc


@pytest.fixture(scope="session")
def thread_user_vk():
    api_svc = APISvc()
    token = api_svc.login(auth_type="vk")
    api_svc.token = token
    return token, api_svc

