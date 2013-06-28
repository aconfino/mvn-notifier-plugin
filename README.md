Description:  This plugin is used to send JSON notifications to a given endpoint.  The notification contains the groupId, artifactId, and version of the artifact as well as the system timestamp.  A common use case would be to send a notification during the deploy goal.

Multiple endpoints can be specified.  The plugin can also be configured to fail the execution (default) if the endpoint was not reached. Here is an example configuration. 
```
<plugin>
   <groupId>com.confino</groupId>
   <artifactId>notifier-maven-plugin</artifactId>
   <version>1.1-SNAPSHOT</version>
      <executions>
         <execution>
            <id>notify-endpoint</id>
            <phase>install</phase>
            <configuration>
               <endpoints>
                  <param>http://localhost:8000/test</param>
                  <param>http://localhost:8001/test</param>
               </endpoints>
               <!-- default is true -->
               <failBuild>false</failBuild>
            </configuration>
            <goals>
               <goal>notify</goal>
            </goals>
         </execution>
      </executions>
</plugin>
```

To run
```
mvn com.confino:notifier-maven-plugin:notify
```