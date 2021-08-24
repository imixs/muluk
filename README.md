# Muluk-Monintor

<img src="./doc/resources/MAYA-g-log-cal-D09-Muluk.png" />

Muluk is a super simple WebService Monitor. Muluk watches your web services and applications and informs you if something get wrong. It is independent form any other service, needs not database and no painful installation. You simply start with docker and one configuration file:



# How to Start

Muluk is hosted on Docker-Hub. So you simply need Docker to run Muluk as a Web Service on any kind of server within you private network or in the itnernet. 

 

    docker run....

## The Configuration

All you need is a configuration file defining the targets to monitor.    


    <muluk-dev>
    
       <mail>
         <smtp>...</smtp>
       </mail>
       
       
       <targets>
          <target type="http">
            <source>http://xxxxx</source>
            <auth type="basic">
               <user>yyy</user>
               <password>xxx</password>
            </auth>
            <expected>....regex...</expected>
          </target>
       </targets>
    </muluk-def>

    
    
# Development

The Imixs-Muluk project is open source and you are invited to join the development on [Github](https://github.com/imixs/muluk). If you have questions please use the [Discussion Forum](https://github.com/imixs/muluk/discussions).

Muluk is a Maven Project developed in Java and based on Jakarta EE. We use Docker to provide a single container to run the tool.

## How To Build

To build from sources run:

	$ docker build -t imixs/muluk ./
	
to start the container run:


	$ docker run --env VARIABLE1=foobar imixs/muluk:latest 
		

## Local Development

During development you can build a dev version providing debugging mode. To build the dev version run:

	docker build -f Dockerfile-Dev -t imixs/muluk ./

to start the container in dev mode run:


	$ docker run --env MULUK_CONFIG_FILE="/opt/jboss/wildfly/config.xml" imixs/muluk:latest 
		
MULUK_CONFIG_FILE: 

    