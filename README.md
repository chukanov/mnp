# Java библиотека для определения мобильного оператора и домашнего региона абонента

Поддерживаются
* реестры DEF кодов Россвязи (свежие реестры всегда находятся по адресу: http://www.rossvyaz.ru/docs/articles/Kody_DEF-9kh.csv)
* вручную задаваемые маски в конфигурации (config/mnos.xml)
* реестры БДПН номеров от https://zniis.ru/. Порядок доступа к БДПН описан тут: https://zniis.ru/bdpn/pay-system/access-procedure.
Если перед вами задача определения только региона абонента, то БДПН не потребуется, так как портировать номер абоненты могут только внутри своего домашнего региона.

В комплекте есть сервер, для определения оператора и региона через REST API (./web-service).

Telegram бот на основе библиотеки: https://telegram.me/MNProbot, бот создан на платформе https://miniapps.run 

## Требования
Java 8+

## Зависимости
* log4j-1.2.X
* junit-4.X для тестов после сборки

## Cборка
    ant

## Дополнительно

В качестве хранилища используется оперативная память.
При работе только с реестрами Россвязи требуется не более 20 – 50 Мб ОЗУ.
При работе с БДПН, требуется около 0.6 - 1.3 Гб ОЗУ.

Файлы масок автоматически отслеживаются на изменение.
При создании хранилища можно указать политику изменения хранилища:
* DISABLED: Отключить отслеживание изменений файлов;
* BUILD_AND_REPLACE: Включено по умолчанию. При изменении файла, создать наряду с текущим новое хранилище и когда оно будет готово – заменить. В этом варианте не будет перерывов в работе, но требуется больше ОЗУ.
* CLEAR_AND_BUILD: При изменении файлов, хранилище блокируется, чистится и заполняется заново.

Пример создания хранилища:

        Storage storage = Builder.
                builder().
                add(new RossvyazMasksParser(Paths.get("config/rossvyaz/Kody_DEF-9kh.csv"))).
                add(new CustomMasksParser(Paths.get("config/mnos.xml"))).
                add(new ZniisMnpParser(Paths.get("config/zniils/"))).
                idTitle( Paths.get("config/idfilters/titles.xml")).
                idRegion(Paths.get("config/idfilters/areas.xml")).
                build();
        Mno mno = storage.lookup("79139367911"));
        System.out.println(mno);
        

# Сервер REST API
## Зависимости
* jersey-server-2.7
* jetty-all-9.3.0.M1

## Сборка
    ant

## Запуск
После сборки выполнить:

    cd distr
    start.sh

## Остановка
После запуска выполнить:

    cd distr
    stop.sh
