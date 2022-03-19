import json
from appium import webdriver

import pytest
import requests

from loguru import logger

from tests.api.APISvc import APISvc
from tests.api.constants import BASE_URL
from tests.ui.constants import DESIRED_CAPABILITIES, WEB_ADDRESS, locators


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


@pytest.fixture(scope="function")
def payshare_window_login():
    driver = webdriver.Remote(WEB_ADDRESS, desired_capabilities=DESIRED_CAPABILITIES)
    driver.implicitly_wait(10)

    return driver


@pytest.fixture(scope="function")
def payshare_window_after_login_for_thread_vk():
    driver = webdriver.Remote(WEB_ADDRESS, desired_capabilities=DESIRED_CAPABILITIES)
    driver.implicitly_wait(10)

    element = driver.find_element_by_id(locators["vk_auth"])
    element.click()
    driver.implicitly_wait(5)
    return driver


@pytest.fixture(scope="function")
def payshare_window_after_login_for_thread_google():
    driver = webdriver.Remote(WEB_ADDRESS, desired_capabilities=DESIRED_CAPABILITIES)
    driver.implicitly_wait(10)

    element = driver.find_element_by_id(locators["google_auth"])
    element.click()
    driver.implicitly_wait(5)
    return driver
