<h1 style="text-align: center; color: purple; width: 75%; margin-bottom: -20px">java-explore-with-me
<img src="files/1f50e.png" height="27"></h1>
<h3 style="text-align: center; width: 73%; margin-bottom: auto"> This project has <u>two modules:</u> </h3> 

1. <span style="color:#0000FF">"**ewm-service**"</span> - main service in the project.
This module is divided into public, private (for authorized users) and administrative parts:
* The <u>*public*</u> part is responsible for getting short information about events, categories, compilations of events.
* The <u>*private*</u> part is responsible for creating, updating, getting events by initiators and updating event requests;
  creating, getting and updating requests by authors.
* The <u>*admin*</u> part is responsible for creating, deleting and updating categories; getting and updating events; 
creating, updating, deleting users and compilations of events.   
This module is connected to DB "explore-with-me-database".  
2. <span style="color:#0000FF">"**stats**"</span> - statistic service.
This module includes three submodules: 
* <u>"*stat-server*"</u> to save and send statistical data.  
  This submodule is connected to DB "stats-database".
* <u>"*stat-dto*"</u> where DTOs are described for "stat-service".
* <u>"*stat-client*"</u> for sending request to "stat-service".
