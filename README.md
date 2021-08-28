# Muluk-Monintor

<img src="./doc/resources/logo.png" />

Muluk is a super simple WebService Monitor. Muluk watches your web services and applications and informs you if something get wrong. It is independent form any other service, needs not database and no painful installation. You simply start with docker and one configuration file:

<img src="./doc/resources/screen-01.png" /> 


# How to Start

Muluk is hosted on Docker-Hub. So you simply need Docker to run Muluk as a Web Service on any kind of server within you private network or in the Internet. 

	$ docker run \
	  -e TZ="CET" \
	  -e LANG="en_US.UTF-8" \
	  -v $PWD/config.xml:/opt/jboss/config.xml \
	  -p "8080:8080" \
	  imixs/muluk:latest


## The Configuration

All you need is a configuration file defining the targets to monitor.    


    <muluk-dev>
		<cluster name="local-dev">
			<node>
				<target>http://localhost:8080</target>
				<auth type="BASIC" user="admin" password="adminadmin" />
			</node>
		</cluster>
		
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


### Security

The Web interface is protected with a BASIC authentication security realm. You can use the default user 'admin' with the default password 'adminadmin'.

To change the user/password you simply need to create/edit the files '*muluk-users.properties*' and '*muluk-roles.properties*' and map these files into your docker container.

	$ docker run \
	  -e TZ="CET" \
	  -e LANG="en_US.UTF-8" \
	  -v $PWD/config.xml:/opt/jboss/config.xml \
	  -v $PWD/muluk-users.properties:/opt/jboss/wildfly/standalone/configuration/muluk-users.properties \
	  -v $PWD/muluk-roles.properties:/opt/jboss/wildfly/standalone/configuration/muluk-roles.properties \
	  -p "8080:8080" \
	  imixs/muluk:latest

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
	  -v $PWD/docker/configuration/config.xml:/opt/jboss/config.xml \
	  -p "8080:8080" \
	  imixs/muluk:latest

**Note:** The MULUK_CONFIG_FILE must point to your local config.xml file




## Rest API

You can request the current config state with the Rest API endpoint:

	http://localhos:8080/api/config
	
This will return the XML object including the latest monitoring data. 	

## Local Development

During development you can build a dev version providing debugging mode. To build the dev version run:

	$ mvn clean install -Pdebug

to start the container in dev mode run:


	$ docker run \
	  -e TZ="CET" \
	  -e LANG="en_US.UTF-8" \
	  -v $PWD/docker/deployments:/opt/jboss/wildfly/standalone/deployments/ \
	  -v $PWD/docker/configuration/config.xml:/opt/jboss/config.xml \
	  -p "8080:8080" \
	  -p "8787:8787" \
	  imixs/muluk:latest
	  
	 
		

    