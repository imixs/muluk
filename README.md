# Muluk-Monintor

<img src="./doc/resources/logo.png" />

Muluk is a super simple WebService Monitor. Muluk watches your web services and applications and informs you if something get wrong. It is independent form any other service, needs not database and no painful installation. You simply start with docker and one configuration file:

<img src="./doc/resources/screen-01.png" /> 


# How to Start

Muluk is hosted on Docker-Hub. So you simply need Docker to run Muluk as a Web Service on any kind of server within you private network or in the itnernet. 

 

    docker run....

## The Configuration

All you need is a configuration file defining the targets to monitor.    


    <muluk-dev>
    
       <mail>
         <smtp>...</smtp>
       </mail>
       
		<monitor>
			<object type="web" >
				<target>https://www.imixs.org</target>
				<pattern>Imixs-Workflow supports the BPMN 2.0 standard</pattern>
			</object>
		
			<object type="web">
				<target>https://foo.com/</target>
				<pattern>my-data</pattern>
            <auth type="basic">
               <user>yyy</user>
               <password>xxx</password>
            </auth>
			</object>
		</monitor>
	       
    </muluk-def>




## Kubernetes

To run Muluk in Kubernetes first create the config.xml file and put it into a config-map

	$ kubectl create namespace muluk
	$ kubectl create configmap muluk-config --from-file=./muluk/config.xml -n muluk

    
    
# Development

The Imixs-Muluk project is open source and you are invited to join the development on [Github](https://github.com/imixs/muluk). If you have questions please use the [Discussion Forum](https://github.com/imixs/muluk/discussions).

Muluk is a Maven Project developed in Java and based on Jakarta EE. We use Docker to provide a single container to run the tool.

## How To Build

To build from sources run:

	$ mvn clean install -Pdocker
		
to start the container run:


	$ docker run \
	  -e TZ="CET" \
	  -e LANG="en_US.UTF-8" \
	  -e MULUK_CONFIG_FILE="/opt/jboss/wildfly/config.xml" \
	  -v $PWD/docker/configuration/config.xml:/opt/jboss/wildfly/config.xml \
	  -p "8080:8080" \
	  imixs/muluk:latest

**Note:** The MULUK_CONFIG_FILE must point to your local config.xml file

## Local Development

During development you can build a dev version providing debugging mode. To build the dev version run:

	$ mvn clean install -Pdebug

to start the container in dev mode run:


	$ docker run \
	  -e TZ="CET" \
	  -e LANG="en_US.UTF-8" \
	  -e MULUK_CONFIG_FILE="/opt/jboss/wildfly/config.xml" \
	  -v $PWD/docker/deployments:/opt/jboss/wildfly/standalone/deployments/ \
	  -v $PWD/docker/configuration/config.xml:/opt/jboss/wildfly/config.xml \
	  -p "8080:8080" \
	  -p "8787:8787" \
	  imixs/muluk:latest
	  
	 
		

    