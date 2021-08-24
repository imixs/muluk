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