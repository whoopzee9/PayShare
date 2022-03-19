import random

import pytest
import pytest_check as check

from tests.api.markers import *
from tests.ui.constants import locators



class TestUI:

    @pytest.mark.parametrize("auth_type", ["vk", "google"])
    def test_auth(self, payshare_window_login, auth_type):
        driver = payshare_window_login
        element = driver.find_element_by_id(locators[f"{auth_type}_auth"])
        text = element.get_attribute("text").lower()
        auth_type = "вк" if auth_type == "vk" else auth_type
        check.is_in(auth_type, text)
        element.click()
        driver.implicitly_wait(5)
        title = driver.find_element_by_id(locators["page_title"]).get_attribute("text")
        check.equal("Покупки", title)

    def test_profile_vk(self, payshare_window_after_login_for_thread_vk):
        driver = payshare_window_after_login_for_thread_vk

        driver.find_element_by_id(locators["profile_button"]).click()
        driver.implicitly_wait(100)
        profile_name = driver.find_element_by_id(locators["profile_name"]).get_attribute("text")
        name = "test vk"
        check.equal(name, profile_name.lower())

    def test_profile_google(self, payshare_window_after_login_for_thread_google):
        driver = payshare_window_after_login_for_thread_google

        driver.find_element_by_id(locators["profile_button"]).click()
        driver.implicitly_wait(100)
        profile_name = driver.find_element_by_id(locators["profile_name"]).get_attribute("text")
        name = "test google"
        check.equal(name, profile_name.lower())

    def test_click_search_button(self, payshare_window_after_login_for_thread_google):
        driver = payshare_window_after_login_for_thread_google

        driver.find_element_by_id(locators["second_toolbar_button"]).click()
        driver.implicitly_wait(5)
        text = driver.find_element_by_id(locators["code_window_title"]).get_attribute("text")
        expected = "код комнаты"
        check.is_in(expected, text.lower())

    def test_click_qr_button(self, payshare_window_after_login_for_thread_google):
        driver = payshare_window_after_login_for_thread_google

        driver.find_element_by_id(locators["first_toolbar_button"]).click()
        driver.implicitly_wait(5)
        driver.find_element_by_id("com.android.permissioncontroller:id/permission_allow_button").click()
        title = driver.find_element_by_id(locators["page_title"]).get_attribute("text")
        expected = "скан"
        check.is_in(expected, title.lower())

    @th_current
    def test_enter_invalid_pass_code(self, payshare_window_after_login_for_thread_google):
        driver = payshare_window_after_login_for_thread_google

        driver.find_element_by_id(locators["second_toolbar_button"]).click()
        driver.implicitly_wait(5)
        ok_button = driver.find_element_by_id(locators["code_ok_button"])

        check.equal(ok_button.get_attribute("enabled").lower(), "false")
        for i in range(1, 5):
            data = 1 * 10 ** (i-1)
            driver.find_element_by_id(locators["code_text"]).clear()
            driver.find_element_by_id(locators["code_text"]).send_keys(data)
            check.equal(ok_button.get_attribute("enabled").lower(), "false")
            driver.implicitly_wait(500)

        driver.find_element_by_id(locators["code_text"]).clear()
        driver.find_element_by_id(locators["code_text"]).send_keys(12345)
        check.equal(ok_button.get_attribute("enabled").lower(), "true")
        ok_button.click()
        driver.implicitly_wait(300)
        ok_button = driver.find_element_by_id(locators["code_ok_button"])
        check.equal(ok_button.get_attribute("enabled").lower(), "true")

    def test_click_add_room_button(self, payshare_window_after_login_for_thread_google):
        driver = payshare_window_after_login_for_thread_google

        driver.find_element_by_id(locators["add_float_button"]).click()

        driver.implicitly_wait(1000)
        element = driver.find_element_by_id(locators["save_room_button"])
        check.equal(element.get_attribute("text").lower(), "сохранить")
        check.equal(element.get_attribute("enabled").lower(), "false")

    def test_check_enable_add_room(self, payshare_window_after_login_for_thread_google):
        driver = payshare_window_after_login_for_thread_google

        driver.find_element_by_id(locators["add_float_button"]).click()

        driver.implicitly_wait(1000)
        element_button = driver.find_element_by_id(locators["save_room_button"])
        element_name = driver.find_element_by_id(locators["add_room_name"])
        element_date = driver.find_element_by_id(locators["add_room_date"])
        element_time = driver.find_element_by_id(locators["add_room_time"])
        text = element_name.get_attribute("text")
        expected = "название"
        check.equal(text.lower(), expected)
        check.is_true(element_date.get_attribute("text"))
        check.is_true(element_time.get_attribute("text"))
        check.equal(element_button.get_attribute("enabled").lower(), "false")

        element_name.send_keys("Новая комната")
        check.equal(element_button.get_attribute("enabled").lower(), "true")

    @pytest.mark.parametrize("room_type", ["open", "close"])
    def test_check_click_room_card(self, payshare_window_after_login_for_thread_google, room_type):
        driver = payshare_window_after_login_for_thread_google

        locator = locators["opened_rooms_button"] if room_type == "open" else locators["archieve_button"]
        driver.find_element_by_id(locator).click()
        rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
        room = random.choice(rooms)

        room_name = room.get_attribute("text")
        room.click()
        driver.implicitly_wait(1000)
        title = driver.find_element_by_id(locators["page_title"]).get_attribute("text")
        check.equal(room_name, title)

        driver.find_element_by_id(locators["second_toolbar_button"]).click()

        if room_type == "open":
            pass_code = driver.find_element_by_id(locators["share_code"]).get_attribute("text")
            check.equal(len(pass_code), 5)
        else:
            with pytest.raises(Exception) as e:
                pass_code = driver.find_element_by_id(locators["share_code"]).get_attribute("text")
            return

        driver.find_element_by_id(locators["back_toolbar_button"]).click()
        driver.find_element_by_id(locators["second_toolbar_button"]).click()

        pass_code_second = driver.find_element_by_id(locators["share_code"]).get_attribute("text")
        check.equal(pass_code, pass_code_second)

    def test_check_passcode_after_creating_room(self, payshare_window_after_login_for_thread_google):
        driver = payshare_window_after_login_for_thread_google

        driver.find_element_by_id(locators["add_float_button"]).click()

        driver.implicitly_wait(1000)
        element_name = driver.find_element_by_id(locators["add_room_name"])
        room_name = "test-room{datetime.datetime.now()}"
        element_name.send_keys(room_name)

        driver.find_element_by_id(locators["save_room_button"]).click()
        title = driver.find_element_by_id(locators["page_title"]).get_attribute("text")
        check.equal(room_name, title)

        driver.find_element_by_id(locators["second_toolbar_button"]).click()

        pass_code = driver.find_element_by_id(locators["share_code"]).get_attribute("text")
        check.equal(len(pass_code), 5)

        driver.find_element_by_id(locators["back_toolbar_button"]).click()
        driver.find_element_by_id(locators["second_toolbar_button"]).click()

        pass_code_second = driver.find_element_by_id(locators["share_code"]).get_attribute("text")
        check.equal(pass_code, pass_code_second)

    def test_click_add_purchase_button(self, payshare_window_after_login_for_thread_google):
        driver = payshare_window_after_login_for_thread_google

        driver.find_element_by_id(locators["add_float_button"]).click()

        driver.implicitly_wait(1000)
        element = driver.find_element_by_id(locators["save_room_button"])
        check.equal(element.get_attribute("text").lower(), "сохранить")
        check.equal(element.get_attribute("enabled").lower(), "false")


    def test_check_enable_add_purchase(self, payshare_window_after_login_for_thread_google):
        driver = payshare_window_after_login_for_thread_google

        driver.find_element_by_id(locators["add_float_button"]).click()

        driver.implicitly_wait(1000)
        element_button = driver.find_element_by_id(locators["save_room_button"])
        element_name = driver.find_element_by_id(locators["add_room_name"])
        element_date = driver.find_element_by_id(locators["add_room_date"])
        element_time = driver.find_element_by_id(locators["add_room_time"])
        text = element_name.get_attribute("text")
        expected = "название"
        check.equal(text.lower(), expected)
        check.is_true(element_date.get_attribute("text"))
        check.is_true(element_time.get_attribute("text"))
        check.equal(element_button.get_attribute("enabled").lower(), "false")

        element_name.send_keys("Новая комната")
        check.equal(element_button.get_attribute("enabled").lower(), "true")

