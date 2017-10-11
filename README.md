# Java library for determining mobile operator and home region by MSISDN
You can try how the library works in Telegram bot: https://telegram.me/MNProbot.

My article about used structures and algorithms: https://habrahabr.ru/post/337338

The library supports:
* Rossvyaz DEF codes (fresh registers are always located at: http://www.rossvyaz.ru/docs/articles/Kody_DEF-9kh.csv)
* manually set masks in the configuration (config/mnos.xml)
* Data Base of Ported Numbers from https://zniis.ru/. The order of access to the DB is described here: https://zniis.ru/bdpn/pay-system/access-processure.
If you need to determining the subscriber's region only, the the ZNIIS DB will not be required, since subscribers can only port the number within their home region.

Also there is a jetty server, to determine the operator and region through the REST API (services/web-service).

## Requirements
Java 8+

## Dependencies
* log4j-1.2.X
* junit-4.X

## Build script
    ant

## Tech description

RAM is used as a storage.
When working only with Rossvyaz registers the storage requires no more than 20 - 50 MB of RAM.
When working with ZNIIS DB, it takes about 0.6 - 1.3 GB of RAM.

The mask files are automatically tracked for change.
When creating a repository, you can specify a policy for changing the repository:
* DISABLED: Disable tracking of file changes;
* BUILD_AND_REPLACE: Enabled by default. If you change the file, create along with the current new storage and when it is ready - replace. In this version, there will be no interruptions in operation, but more RAM is required.
* CLEAR_AND_BUILD: When changing files, the storage is locked, cleaned and refilled.

Example of creating a repository:
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
        

# REST API server
## Dependencies
* jersey-server-2.7
* jetty-all-9.3.0.M1

## Build script
    cd services/web-service
    ant

## Run the server
    cd distr
    start.sh

## Stop the server
    cd distr
    stop.sh
