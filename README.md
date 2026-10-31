
# Fascicolo Sanitario 2.0

# _it-fse-gtw-rules-manager_

Il microservizio in oggetto ha la responsabilità di recuperare dall'EDS le regole sintattiche, semantiche, terminologiche e di mapping.

<br/>

## Repos
- [*it-fse-support*](https://github.com/ministero-salute/it-fse-support) - Pagina di supporto FSE 2.0
- [*it-fse-landing*](https://github.com/ministero-salute/it-fse-landing) - Landing page dei repository FSE 2.0

<br/>

## Test ISOLATION

Con Java 21 e Maven, eseguire `MAVEN_OPTS=-Xmx768m mvn -PISOLATION clean test` per avviare solo i
test JUnit con tag `ISOLATION`. Sono esclusi da Maven per default; i test
preesistenti restano invariati. I contesti Spring caricano esclusivamente i
servizi/repository necessari con profilo `ISOLATION`, senza avviare l'applicazione,
scheduler, MongoDB o Kafka. Le richieste HTTP usano `MockRestServiceServer`,
mentre repository e driver Mongo sono simulati ai confini esterni.

La suite verifica configurazione/cache, client HTTP e relativi errori,
conversioni e sintesi dei dati, query/indici Mongo, piani/retry e utility,
workflow completi degli executor EDS e recupero da errori, orchestrazione dello
scheduler, controller HTTP e risposte Problem JSON, logging strutturato e
consegna Kafka con produttori simulati.
Il report JaCoCo è disponibile in `target/site/jacoco/index.html` (XML nella stessa
directory).

## License

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL_v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
