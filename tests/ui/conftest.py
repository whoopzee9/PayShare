import json
import random

from appium import webdriver

import pytest
import requests

from loguru import logger

from tests.api.APISvc import APISvc
from tests.api.constants import BASE_URL
from tests.ui.constants import DESIRED_CAPABILITIES, locators


def pytest_addoption(parser):
    parser.addoption(
        "--device_name", action="store", default="DUM0219A22002975", help="Name of device"
    )
    parser.addoption(
        "--web_address", action="store", default="http://localhost:4723/wd/hub", help="Appium server address"
    )
    parser.addoption("--app", action="store",
                     default="C:\\Users\\Григорий\\Downloads\\Telegram Desktop\\app-release (2).apk",
                     help="Apk path"
    )

@pytest.fixture(scope="session")
def device_name(request):
    yield request.config.getoption("--device-name")


@pytest.fixture(scope="session")
def web_address(request):
    yield request.config.getoption("--web_address")


@pytest.fixture(scope="session")
def app(request):
    yield request.config.getoption("--app")


@pytest.fixture(scope="session")
def desired_capabilities(request):
    desired_capabilities = DESIRED_CAPABILITIES
    desired_capabilities["appium:deviceName"] = request.config.getoption("device_name")
    desired_capabilities["appium:app"] = request.config.getoption("app")
    yield desired_capabilities

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
def payshare_window_login(web_address, desired_capabilities):
    driver = webdriver.Remote(web_address, desired_capabilities=desired_capabilities)
    driver.implicitly_wait(20)

    return driver


@pytest.fixture(scope="function")
def payshare_window_after_login_for_thread_vk(web_address, desired_capabilities):
    driver = webdriver.Remote(web_address, desired_capabilities=desired_capabilities)
    driver.implicitly_wait(30)

    element = driver.find_element_by_id(locators["vk_auth"])
    element.click()
    driver.implicitly_wait(30)
    return driver


@pytest.fixture(scope="function")
def payshare_window_after_login_for_thread_google(web_address, desired_capabilities):
    driver = webdriver.Remote(web_address, desired_capabilities=desired_capabilities)
    driver.implicitly_wait(30)

    element = driver.find_element_by_id(locators["google_auth"])
    element.click()
    driver.implicitly_wait(30)
    return driver

@pytest.fixture(scope="function")
def payshare_opened_room_for_thread_vk(payshare_window_after_login_for_thread_vk):
    driver = payshare_window_after_login_for_thread_vk
    driver.implicitly_wait(30)
    driver.find_element_by_id(locators["opened_rooms_button"]).click()
    rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
    random.choice(rooms).click()
    driver.implicitly_wait(30)
    return driver

@pytest.fixture(scope="function")
def payshare_opened_room_for_thread_google(payshare_window_after_login_for_thread_google):
    driver = payshare_window_after_login_for_thread_google
    driver.implicitly_wait(30)
    driver.find_element_by_id(locators["opened_rooms_button"]).click()
    rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
    random.choice(rooms).click()
    driver.implicitly_wait(30)
    return driver

@pytest.fixture(scope="function")
def payshare_closed_room_for_thread_vk(payshare_window_after_login_for_thread_vk):
    driver = payshare_window_after_login_for_thread_vk
    driver.implicitly_wait(30)

    driver.find_element_by_id(locators["archive_button"]).click()
    rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
    room = random.choice(rooms)
    room.click()
    driver.implicitly_wait(30)
    return driver

@pytest.fixture(scope="function")
def payshare_closed_room_for_thread_google(payshare_window_after_login_for_thread_google):
    driver = payshare_window_after_login_for_thread_google

    driver.find_element_by_id(locators["archive_button"]).click()
    rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
    room = random.choice(rooms)
    room.click()
    driver.implicitly_wait(30)
    return driver

