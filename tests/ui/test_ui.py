import datetime
import random
import uuid

import pytest_check as check
from appium.webdriver.common.touch_action import TouchAction

from tests.api.markers import *
from tests.ui.constants import locators
from loguru import logger


@th_current
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
            driver.implicitly_wait(5)
            button_state = ok_button.get_attribute("enabled").lower()
            check.equal(button_state, "false")
            driver.implicitly_wait(50)

        driver.find_element_by_id(locators["code_text"]).clear()
        driver.find_element_by_id(locators["code_text"]).send_keys(12345)
        check.equal(ok_button.get_attribute("enabled").lower(), "true")
        ok_button.click()
        driver.implicitly_wait(300)
        ok_button = driver.find_element_by_id(locators["code_ok_button"])
        check.equal(ok_button.get_attribute("enabled").lower(), "true")

    def test_click_add_room_button(self, payshare_window_after_login_for_thread_google):
        driver = payshare_window_after_login_for_thread_google
        driver.implicitly_wait(10)
        driver.find_element_by_id(locators["add_float_button"]).click()

        driver.implicitly_wait(10)
        title = driver.find_element_by_id(locators["event_dialog_title"])
        check.is_in("событие", title.get_attribute("text").lower())


    def test_check_enable_add_room(self, payshare_window_after_login_for_thread_google):
        driver = payshare_window_after_login_for_thread_google
        driver.implicitly_wait(10)
        driver.find_element_by_id(locators["add_float_button"]).click()

        driver.implicitly_wait(10)
        element_button = driver.find_element_by_id(locators["save_room_button"])
        element_name = driver.find_element_by_id(locators["add_room_name"])
        element_date = driver.find_element_by_id(locators["add_room_date"])
        element_time = driver.find_element_by_id(locators["add_room_time"])
        text = element_name.get_attribute("text")
        expected = "название"
        check.equal(text.lower(), expected)
        check.is_in(element_button.get_attribute("text").lower(), ["сохранить", "добавить"])
        check.equal(element_button.get_attribute("enabled").lower(), "false")
        check.is_true(element_date.get_attribute("text"))
        check.is_true(element_time.get_attribute("text"))
        check.equal(element_button.get_attribute("enabled").lower(), "false")

        element_name.send_keys("Новая комната")
        check.equal(element_button.get_attribute("enabled").lower(), "true")

    @pytest.mark.parametrize("room_type", ["open", "close"])
    def test_check_click_room_card(self, payshare_window_after_login_for_thread_google, room_type):
        driver = payshare_window_after_login_for_thread_google
        driver.implicitly_wait(10)
        locator = locators["opened_rooms_button"] if room_type == "open" else locators["archieve_button"]
        driver.find_element_by_id(locator).click()
        rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
        room = random.choice(rooms)

        room_name = room.get_attribute("text")
        room.click()
        driver.implicitly_wait(10)
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
        room_name = f"test-room{datetime.datetime.now()}"
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

        locator = locators["opened_rooms_button"]
        driver.find_element_by_id(locator).click()
        rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
        random.choice(rooms).click()
        driver.implicitly_wait(5)

        driver.find_element_by_id(locators["add_float_button_room"]).click()

        driver.implicitly_wait(5)
        title = driver.find_element_by_id(locators["add_purchase_title"])
        check.is_in("расход", title.get_attribute("text").lower())

    def test_check_enable_add_purchase(self, payshare_window_after_login_for_thread_google):
        driver = payshare_window_after_login_for_thread_google

        locator = locators["opened_rooms_button"]
        driver.find_element_by_id(locator).click()
        rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
        random.choice(rooms).click()
        driver.implicitly_wait(5)

        driver.find_element_by_id(locators["add_float_button_room"]).click()

        driver.implicitly_wait(5)
        element_button = driver.find_element_by_id(locators["add_purchase_save_button"])
        element_name = driver.find_element_by_id(locators["add_purchase_name"])
        element_date = driver.find_element_by_id(locators["add_purchase_date"])
        element_cost = driver.find_element_by_id(locators["add_purchase_cost"])
        element_shop = driver.find_element_by_id(locators["add_purchase_shop"])

        text_name = element_name.get_attribute("text")
        expected_name = "название"
        check.equal(text_name.lower(), expected_name)
        check.is_in(element_button.get_attribute("text").lower(), ["сохранить", "добавить"])

        check.is_true(element_date.get_attribute("text"))
        text_cost = element_cost.get_attribute("text")
        expected_cost = "0.0"
        text_shop = element_shop.get_attribute("text")
        expected_shop = "магазин"
        check.equal(text_cost.lower(), expected_cost)
        check.equal(text_shop.lower(), expected_shop)
        check.equal(element_button.get_attribute("enabled").lower(), "false")

        element_name.send_keys("Новый расход")
        check.equal(element_button.get_attribute("enabled").lower(), "false")
        element_cost.send_keys(55)
        check.equal(element_button.get_attribute("enabled").lower(), "false")
        element_shop.send_keys("Новый магазин")
        check.equal(element_button.get_attribute("enabled").lower(), "true")

    def test_delete_room(self, payshare_window_after_login_for_thread_vk):
        driver = payshare_window_after_login_for_thread_vk

        locator = locators["opened_rooms_button"]
        driver.find_element_by_id(locator).click()
        rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
        titles = [room.get_attribute("text") for room in rooms]
        random.choice(rooms).click()
        driver.implicitly_wait(5)

        driver.find_element_by_id(locators["edit_float_button"]).click()
        driver.implicitly_wait(5)
        driver.find_element_by_id(locators["first_toolbar_button"]).click()
        driver.implicitly_wait(5)

        text = driver.find_element_by_id(locators["delete_dialog"]).get_attribute("text")
        check.is_in("удалить", text.lower())
        check.is_in("комнату", text.lower())

        cancel_button = driver.find_element_by_id(locators["delete_dialog_cancel"])

        cancel_button.click()
        driver.implicitly_wait(10)
        title = driver.find_element_by_id(locators["page_title"]).get_attribute("text")
        check.is_in("test", title.lower())
        room_name = title
        check.is_in(room_name, titles)

        driver.implicitly_wait(10)
        driver.find_element_by_id(locators["first_toolbar_button"]).click()
        driver.implicitly_wait(5)

        text = driver.find_element_by_id(locators["delete_dialog"]).get_attribute("text")
        check.is_in("удалить", text.lower())
        check.is_in("комнату", text.lower())
        ok_button = driver.find_element_by_id(locators["delete_dialog_ok"])
        ok_button.click()
        driver.implicitly_wait(5)

        title = driver.find_element_by_id(locators["page_title"]).get_attribute("text")
        check.is_in("покупки", title.lower())
        rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
        titles = [room.get_attribute("text") for room in rooms]
        check.is_not_in(room_name, titles)

    def test_create_purchase(self, payshare_window_after_login_for_thread_vk):
        driver = payshare_window_after_login_for_thread_vk

        locator = locators["opened_rooms_button"]
        driver.find_element_by_id(locator).click()
        rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
        random.choice(rooms).click()
        driver.implicitly_wait(5)

        driver.find_element_by_id(locators["add_float_button_room"]).click()

        driver.implicitly_wait(5)
        element_button = driver.find_element_by_id(locators["add_purchase_save_button"])
        element_name = driver.find_element_by_id(locators["add_purchase_name"])
        element_cost = driver.find_element_by_id(locators["add_purchase_cost"])
        element_shop = driver.find_element_by_id(locators["add_purchase_shop"])
        name = f"test-purchase-{uuid.uuid4()}"
        element_name.send_keys(name)
        element_cost.send_keys(100)
        shop_name = "test-shop"
        element_shop.click()
        driver.implicitly_wait(5)
        driver.find_element_by_id(locators["add_shop_title"]).send_keys(shop_name)
        driver.implicitly_wait(5)
        driver.find_element_by_id(locators["add_shop_save_button"]).click()
        driver.implicitly_wait(5)
        check.equal(shop_name, element_shop.get_attribute("text"))
        driver.implicitly_wait(5)
        element_button.click()
        purchases = driver.find_elements_by_id(locators["purchase_name"])
        names = [purchase.get_attribute("text") for purchase in purchases]
        check.is_in(name, names)

    def test_purchase_info(self, payshare_window_after_login_for_thread_vk):
        driver = payshare_window_after_login_for_thread_vk

        locator = locators["opened_rooms_button"]
        driver.find_element_by_id(locator).click()
        rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
        random.choice(rooms).click()
        driver.implicitly_wait(5)
        purchases = []
        try:
            purchases = driver.find_elements_by_id(locators["purchase"])
        except Exception:
            logger.error("No element")

        if len(purchases) == 0:
            driver.find_element_by_id(locators["add_float_button_room"]).click()

            driver.implicitly_wait(5)
            element_button = driver.find_element_by_id(locators["add_purchase_save_button"])
            element_name = driver.find_element_by_id(locators["add_purchase_name"])
            element_cost = driver.find_element_by_id(locators["add_purchase_cost"])
            element_shop = driver.find_element_by_id(locators["add_purchase_shop"])
            name = f"test-purchase-{uuid.uuid4()}"
            element_name.send_keys(name)
            element_cost.send_keys(100)
            element_shop.click()
            driver.implicitly_wait(5)
            driver.find_element_by_id(locators["add_shop_title"]).send_keys("test-shop")
            driver.implicitly_wait(5)
            driver.find_element_by_id(locators["add_shop_save_button"]).click()
            driver.implicitly_wait(5)
            element_button.click()
            purchases = driver.find_elements_by_id(locators["purchase"])

        driver.implicitly_wait(5)
        rand = random.randint(0, len(purchases) - 1) if len(purchases) == 1 else 0
        logger.info(f"{rand=}")
        logger.info(f"{purchases=}")
        purchase = purchases[rand]
        purchases = driver.find_elements_by_id(locators["purchase_name"])
        names = [purchase.get_attribute("text") for purchase in purchases]
        purchase_name = names[rand]
        purchases = driver.find_elements_by_id(locators["purchase_date"])
        dates = [purchase.get_attribute("text") for purchase in purchases]
        purchase_date = dates[rand]
        purchases = driver.find_elements_by_id(locators["purchase_price"])
        prices = [purchase.get_attribute("text") for purchase in purchases]
        purchase_price = prices[rand]

        toucher = TouchAction(driver)
        toucher.long_press(purchase)
        toucher.perform()
        driver.implicitly_wait(10)
        driver.find_element_by_id(locators["purchase_info_button"]).click()
        driver.implicitly_wait(10)
        check.equal(driver.find_element_by_id(locators["purchase_info_name"]).get_attribute("text"), purchase_name)
        check.equal(driver.find_element_by_id(locators["purchase_info_buyer"]).get_attribute("text").lower(), "vk test")
        check.equal(driver.find_element_by_id(locators["purchase_info_price"]).get_attribute("text"), purchase_price)
        check.equal(driver.find_element_by_id(locators["purchase_info_date"]).get_attribute("text")[:6], purchase_date[:6])
        check.equal(driver.find_element_by_id(locators["purchase_info_date"]).get_attribute("text")[-2:0], purchase_date[-2:0])

    def test_delete_purchase(self, payshare_window_after_login_for_thread_vk):
        driver = payshare_window_after_login_for_thread_vk

        locator = locators["opened_rooms_button"]
        driver.find_element_by_id(locator).click()
        rooms = driver.find_elements_by_id(locators["rooms_cards_titles"])
        random.choice(rooms).click()
        driver.implicitly_wait(5)
        purchases = []
        try:
            purchases = driver.find_elements_by_id(locators["purchase"])
        except Exception:
            logger.error("No element")

        if len(purchases) == 0:
            driver.find_element_by_id(locators["add_float_button_room"]).click()

            driver.implicitly_wait(5)
            element_button = driver.find_element_by_id(locators["add_purchase_save_button"])
            element_name = driver.find_element_by_id(locators["add_purchase_name"])
            element_cost = driver.find_element_by_id(locators["add_purchase_cost"])
            element_shop = driver.find_element_by_id(locators["add_purchase_shop"])
            name = f"test-purchase-{uuid.uuid4()}"
            element_name.send_keys(name)
            element_cost.send_keys(100)
            element_shop.click()
            driver.implicitly_wait(10)
            driver.find_element_by_id(locators["add_shop_title"]).send_keys("test-shop")
            driver.implicitly_wait(10)
            driver.find_element_by_id(locators["add_shop_save_button"]).click()
            driver.implicitly_wait(10)
            element_button.click()
            purchases = driver.find_elements_by_id(locators["purchase"])

        driver.implicitly_wait(10)
        rand = random.randint(0, len(purchases)) if len(purchases) == 1 else 0
        purchase = purchases[rand]
        purchases = driver.find_elements_by_id(locators["purchase_name"])
        names = [purchase.get_attribute("text") for purchase in purchases]
        purchase_name = names[rand]

        toucher = TouchAction(driver)
        toucher.long_press(purchase)
        toucher.perform()
        driver.implicitly_wait(10)
        driver.find_element_by_id(locators["purchase_delete_button"]).click()
        text = driver.find_element_by_id(locators["delete_dialog"]).get_attribute("text")
        check.is_in("удалить", text.lower())
        check.is_in("покупку", text.lower())

        driver.find_element_by_id(locators["delete_dialog_cancel"]).click()
        purchases = driver.find_elements_by_id(locators["purchase_name"])
        names = [purchase.get_attribute("text") for purchase in purchases]
        check.is_in(purchase_name, names)
        toucher.long_press(purchase)
        toucher.perform()
        driver.implicitly_wait(5)
        driver.find_element_by_id(locators["purchase_delete_button"]).click()
        text = driver.find_element_by_id(locators["delete_dialog"]).get_attribute("text")
        check.is_in("удалить", text.lower())
        check.is_in("покупку", text.lower())
        driver.find_element_by_id(locators["delete_dialog_ok"]).click()

        driver.implicitly_wait(5)
        purchases = driver.find_elements_by_id(locators["purchase_name"])
        names = [purchase.get_attribute("text") for purchase in purchases]
        check.is_not_in(purchase_name, names)