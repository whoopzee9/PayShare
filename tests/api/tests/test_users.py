from loguru import logger


class TestUsers:

    # Просмотр профиля
    def test_get_user(self, thread_user_google):
        token, api_svc = thread_user_google
        res_data = api_svc.get_user()
        logger.debug(res_data)
        assert all(item in res_data.keys() for item in ["first_name", "second_name", "image_url"])
