<%request.setAttribute("title", "How to get a new P4 account"); %>
<br/>
<p>
PrestoPRIME Preservation Platform (P4) is made up of two main components:
<ul>
<li>the <b>P4 Service</b>, providing HTTP RESTful APIs for ingest, access and other workflows;</li>
<li>the <b>P4 GUI</b>, a Web UI that helps you in interacting with the P4 Service.
</ul>
Through the GUI you can access multiple independent P4 Services, simply switching from one context to another using P4 GUI menu.
</p>
<p>
The user must first create a new account on the P4 GUI; then she can request a userID, in order to access a specific P4 Service; finally she can associate the userID to her P4 GUI account.<br/>
The userID can also be used to interact with the P4 Service by CLI.
</p>
<p>
Follow the instruction below to get your P4 account up and running.
</p>

<div class="minititle">
	Create a new account for P4 GUI
</div>
<ol>
<li>Go to the create account page <a class="coloured" href="https://p4.eurixgroup.com/p4gui/signup" target="_blank">here</a>.</li>

<li>Click on "Sign Up" in the login form to request a new P4 GUI account.</li>

<li>Choose your username, insert a valid e-mail address and press the "Sign Up" button.</li>

<li>Check your e-mail for a new message with the link to activate your account.</li>

<li>Follow the link and fill in the form to activate your account. Insert your contact information (all fields are mandatory), select a password, then press the "Activate account". You are now logged in into P4 GUI. </li>
</ol>

<div class="minititle">
	Create a new userID on P4 Service
</div>
<ol>
<li>After creating the P4 GUI account you will be prompted for registering a new P4 service in order to access a running P4 platform. Provide a valid URL (e.g. https://p4.eurixgroup.com/p4ws) and select desired permission (Access only - Access and Ingest - Access, Ingest and Manage). Request the last option to manage tasks for users and jobs. Press the "Request" button (the request must be approved by P4 admin).</li>

<li>The "Terms of use" for the selected P4 service are provided, read them carefully and accept, then press the "Request" button.</li>

<li>Check your e-mail for a new message with a valid user ID from P4 admin (the request could be manually processed at this stage).</li>

<li>Additional userID for other available P4 Services can be requested using the "MyP4" menu on top-right corner.</li>
</ol>

<div class="minititle">
	Associate your P4 Service userID to your P4 GUI account 
</div>
<ol>
<li>Register the new P4 service using the "MyP4" menu ("Add new P4 Service"): insert the P4 Service URL and userID included in the email and press "Add" button. The bottom menu is automatically populated with other buttons according to the approved permission associated to the userID.</li>

<li>You can see the new registered P4 service in the "Registered P4 Services" area of "MyP4" menu. If you are registered for different P4 Services, you can change the selected one in the "MyP4" menu.</li>
</ol>

<div class="minititle">
	Use the P4 Service from CLI
</div>
<p>
	The only user information required for authorization on P4 Service is the userID, which must be passed as an HTTP Header in the HTTP Request. See examples below for HTTP Requests from CLI using <a class="coloured" href="http://curl.haxx.se">cURL</a>.
</p>
<ul>
<li>Get list of DIP identifiers:
<pre>
	curl -k -H "userID: &lt;userID&gt;" &lt;P4_Service_URL&gt;/access/dip/list
</pre>
</li>

<li>Execute a workflow passing input parameters:
<pre>
	curl -k -H "userID: &lt;userID&gt;" -F "&lt;key&gt;=&lt;string_value&gt;" -F "&lt;key&gt;=@&lt;file_value&lt;" &lt;P4_Service_URL&gt;/wf/execute/&lt;wf_name&gt;
</pre>
</li>
</ul>
<p>
A list of available P4 Service APIs will be provided soon.
</p>
