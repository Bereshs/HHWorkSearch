# HHWorkSearch

Приложение помощи поиск работы на hh.ru с использованием HeadHunter API (https://api.hh.ru/)

Версия 0.0 (инициализация приложения)

Планируемый функционал:
- анализ рекомендуемых вакансий,
- ведение списка просмотренных вакансий,
- ведение и анализ списка откликов на резюме,
- подготовка новых и рекомендуемых вакансий на основе резюме,
- подготовка рекомендаций для резюме на основе вакансий,
- автоматическое поднятие резюме в поиске,

Используемые технологии:
- java 17,
- maven,
- spring boot,
- HeadHunter Api (https://api.hh.ru/),
- postgresSql,
- jpa,
- rest api.

Для работы необходим aplication.properties, со следующим содержанием:
hh.api.url = https://api.hh.ru/
hh.user.agent= 
hh.client.id = 
hh.client.secret =
appEmail.email = 
appEmail.password = 
