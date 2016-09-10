<%@taglib prefix="sec" 	uri="http://www.springframework.org/security/tags" %>
<div id="top-form">
	<form action="j_spring_security_logout" method="post" id="logoutForm">
		<button type="submit" class="btn btn-default pull-right" tabindex="-1"><span class="glyphicon glyphicon-log-out" ></span> Logout</button>
	</form>
	<div class="form-group">
		<form name="selectCustomerForm" class="navbar-form">
			<div class="input-group">
				<span class="input-group-addon">Customer</span>
				<select class="form-control" ng-options="customer for customer in customers" ng-model="selectedCustomer" ng-change="refresh()"></select>
				<sec:authorize access="hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')">
					<span class="input-group-btn">
						<button type="button" class="btn btn-default" ng-show="selectedCustomer" ng-click="storeProperties()" tabindex="-1">
							<span class="glyphicon glyphicon-save-file" aria-hidden="true"></span> Store
						</button>
					</span>
					<span class="input-group-btn">
						<a href="./action/message/export" target="_blank" tabindex="-1">
						<button type="button" class="btn btn-default" ng-show="selectedCustomer" tabindex="-1">
							<span class="glyphicon glyphicon-download" aria-hidden="true"></span> Export
						</button>
						</a>
					</span>
				</sec:authorize>
			</div>
		</form>
	</div>
	<sec:authorize access="hasRole('ROLE_SUPER_ADMIN')">
		<div class="form-group" ng-show="selectedCustomer">
			<form name="localeAddForm" class="navbar-form">
				<div class="input-group">
					<span class="input-group-btn">
						<button type="button" class="btn btn-default" ng-click="loadLocales()" tabindex="-1">
							<span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span> Load
						</button>
					</span>
					<span class="input-group-addon">Locale</span>
					
					<select class="form-control" ng-model="selectedLocale">
						<option ng-repeat="language in locales" value="{{language.key}}">{{language.key}}: {{language.value}}</option>
					</select>
					
					<span class="input-group-btn">
						<button type="button" class="btn btn-default" ng-show="selectedLocale" ng-click="addLocale()" tabindex="-1">
							<span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span> Add
						</button>
						<button type="button" class="btn  btn-default" ng-show="selectedLocale"  ng-confirm-click="Are you sure you want to remove the selected locale?" confirmed-click="deleteLocale()" tabindex="-1" >
							<span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Delete
						</button>
					</span>
				</div>
			</form>
		</div>
	</sec:authorize>
	<sec:authorize access="hasRole('ROLE_SUPER_ADMIN')">
		<div class="form-group" ng-show="selectedCustomer">
			<form name="messageAddForm" class="navbar-form">
				<div class="input-group">
					<span class="input-group-addon">Message</span>
					
					<input type="text" class="form-control" ng-model="message.key" placeholder="key" />
					
					<span class="input-group-addon">=</span>
					
					<input type="text" class="form-control" ng-model="message.value" placeholder="value" />
					
					<span class="input-group-btn">
						<button type="button" class="btn btn-default" ng-click="add(message)" ng-disabled="!message.key" tabindex="-1">
							<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add
						</button>
					</span>
				</div>
				
			</form>
		</div>
	</sec:authorize>
	<div class="form-group" ng-show="selectedCustomer">
		<form name="messageSearchForm" class="navbar-form">
			<div class="input-group">
				<span class="input-group-addon">Filter on</span>
				<input type="text" class="form-control" ng-model="searchValue" ng-change="filterKeys(searchValue)" placeholder="value" />
			</div>
			
		</form>
	</div>
</div>
<div id="editorScreen" ng-show="selectedCustomer">
	<div ng-hide="keys">Properties not yet loaded...</div>
	<select size="2" ng-options="key for key in keys" ng-model="selectedKey" ng-change="fillFormList()" ng-show="keys"></select>
	
	<div id="formList" ng-show="keys">
		<sec:authorize access="hasRole('ROLE_SUPER_ADMIN')">
			<button type="button" class="btn btn-default btn-sm form-button" ng-confirm-click="Are you sure you want to remove this key?" confirmed-click="delete()" ng-show="selectedKey" tabindex="-1">
				<span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Remove this key
			</button>
		</sec:authorize>
		<form name="messageForm" class="control-form" ng-repeat="message in messages">
			<div class="input-group">
				<sec:authorize access="hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')">
					<span class="input-group-btn">
						<button class="btn btn-default" type="button" ng-disabled="messageForm.$pristine" ng-click="reset(message); messageForm.$setPristine();" tabindex="-1">
							<span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>
						</button>
					</span>
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')">
					<input type="text" class="form-control" ng-model="message.value" />
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_VIEWER')">
					<input type="text" class="form-control" ng-model="message.value" disabled="disabled" />
				</sec:authorize>
				<span class="input-group-addon">{{message.locale}}</span>
				<sec:authorize access="hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')">
					<span class="input-group-btn">
						<button class="btn btn-default" type="button" ng-disabled="messageForm.$pristine"  ng-click="update(message); messageForm.$setPristine();" tabindex="-1">
							<span class="glyphicon glyphicon-check" aria-hidden="true"></span>
						</button>
					</span>
				</sec:authorize>
			</div>
		</form>
		
	</div>
</div>
<br />
<a href="manual.pdf" target="_blank" tabindex="-1"><button class="btn btn-info" tabindex="-1"><span class="glyphicon glyphicon-question-sign"></span> Manual</button></a>