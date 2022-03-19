DESIRED_CAPABILITIES = {
    "appium:deviceName": "DUM0219A22002975",
    "platformName": "Android",
    "appium:appPackage": "ru.spbstu.payshare",
    "appium:app": "C:\\Users\\Григорий\\Downloads\\Telegram Desktop\\app-release (2).apk",
    "appium:appWaitActivity": "ru.spbstu.payshare.root.presentation.RootActivity"
}

WEB_ADDRESS = "http://localhost:4723/wd/hub"

locators = {
    "vk_auth": "ru.spbstu.payshare:id/frg_login__mb_vk",
    "google_auth": "ru.spbstu.payshare:id/frg_login__mb_google",
    "profile_button": "ru.spbstu.payshare:id/profileFragment",
    "opened_rooms_button": "ru.spbstu.payshare:id/eventsFragment",
    "archieve_button": "ru.spbstu.payshare:id/historyFragment",
    "rooms_cards": "ru.spbstu.payshare:id/item_events__card_view",
    "rooms_cards_titles": "ru.spbstu.payshare:id/item_events__tv_event_title",
    "profile_name": "ru.spbstu.payshare:id/frg_profile__tv_name",
    "page_title": "ru.spbstu.payshare:id/include_toolbar__tv_title",
    "event_dialog_title": "ru.spbstu.payshare:id/frg_add_event_dialog__tv_title",
    "first_toolbar_button": "ru.spbstu.payshare:id/include_toolbar__ib_first_button",
    "second_toolbar_button": "ru.spbstu.payshare:id/include_toolbar__ib_second_button",
    "back_toolbar_button": "ru.spbstu.payshare:id/include_toolbar__ib_back_button",
    "code_text": "ru.spbstu.payshare:id/frg_search_event_dialog__pin_field",
    "code_ok_button": "ru.spbstu.payshare:id/frg_serch_event_dialog__mb_ok",
    "code_window_title": "ru.spbstu.payshare:id/frg_search_event_dialog__tv_title",
    "add_float_button": "ru.spbstu.payshare:id/frg_events__fab_add",
    "edit_float_button": "ru.spbstu.payshare:id/frg_event__fab_edit",
    "save_room_button": "ru.spbstu.payshare:id/frg_add_event_dialog__mb_save",
    "add_room_name": "ru.spbstu.payshare:id/frg_add_event_dialog__et_title",
    "add_room_date": "ru.spbstu.payshare:id/frg_add_event_dialog__et_date",
    "add_room_time": "ru.spbstu.payshare:id/frg_add_event_dialog__et_time",
    "share_code": "ru.spbstu.payshare:id/frg_qr_code_sharing__tv_code"
}