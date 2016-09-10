var localePropertiesEditor = angular.module('localePropertiesEditor', ['ngRoute']);
	
localePropertiesEditor.config(function ($routeProvider) {
    $routeProvider.when('/messages', { controller: 'MessageController', templateUrl: 'app/html/message.jsp'});
});

localePropertiesEditor.directive('ngConfirmClick', [
                                 function(){
                                     return {
                                         link: function (scope, element, attr) {
                                             var msg = attr.ngConfirmClick || "Are you sure?";
                                             var clickAction = attr.confirmedClick;
                                             element.bind('click',function (event) {
                                                 if ( window.confirm(msg) ) {
                                                     scope.$eval(clickAction);
                                                 }
                                             });
                                         }
                                     };
                             }]);


localePropertiesEditor.controller('MessageController', function ($scope, $http, $compile) {
    var actionUrl = 'action/message/';
    
    $scope.checkData = function(data) {
		try {
			angular.fromJson(data);			
		} catch (e) {
			alert("You are no longer logged in...");
			window.location.href = "login.html";
		}
	};
    
    $scope.loadCustomers = function () {
	    $http.get(actionUrl + "customers").success(function (data) {
			$scope.checkData(data);
			$scope.customers = data;
			if ($scope.customers.length==1) {
				$scope.selectedCustomer=$scope.customers[0];
				$scope.refresh();
			}
	    });
    };
    $scope.storeProperties = function () {
        $http.post(actionUrl + "storeProperties").success(function () {
        	$scope.refresh();
        });
    };
    
    // locale specific calls
    $scope.loadLocales = function () {
        $http.get(actionUrl + "locales").success(function (data) {
			$scope.checkData(data);
			
			$scope.locales = data;
        });
    };
    $scope.addLocale = function () {
        $http.post(actionUrl + "addLocale", $scope.selectedLocale).success(function () {
        	$scope.refresh();
        	$scope.selectedLocale=null;
        	$scope.localeAddForm.$setPristine();
        });
    };
    $scope.deleteLocale = function () {
    	$http.delete(actionUrl + "deleteLocale?locale=" +  $scope.selectedLocale).success(function () {
        	$scope.refresh();
        	$scope.selectedLocale=null;
        	$scope.localeAddForm.$setPristine();
        });
    };
    
    // properties/key specific calls
    $scope.refresh = function () {
		$scope.keys=null;
        $http.get(actionUrl + "load/" + $scope.selectedCustomer).success(function (data) {
			$scope.checkData(data);
			
			$scope.keys = data;
			
			$scope.selectedKey = null;
        	$scope.messages = null;
        });
    };
    $scope.reload = function () {
		$scope.keys=null;
        $http.get(actionUrl).success(function (data) {
			$scope.checkData(data);
			
			$scope.keys = data;
			
        	$scope.selectedKey = null;
        	$scope.messages = null;
        });
    };
    $scope.fillFormList = function () {
    	$http.get(actionUrl + "fill?key=" + $scope.selectedKey).success(function (data, status) {
			$scope.checkData(data);
			$scope.messages = data;
        });
    }; 
    
    // message properties specific calls
    $scope.add = function () {
        $http.post(actionUrl + "add", $scope.message).success(function () {
        	$scope.reload();
        	$scope.message = null;
        	$scope.messageAddForm.$setPristine();
        });
    };
    $scope.update = function (message) {
        $http.post(actionUrl + "update", message).success(function () {
        }).error(function (data) {
        	$scope.refresh();
        });
    };
    $scope.reset = function (message) {
    	message.value= message.unchangedValue;
    };
    $scope.delete = function () {
        $http.delete(actionUrl + "delete?key=" + $scope.selectedKey).success(function () {
        	$scope.reload();
        	$scope.selectedKey = null;
        	$scope.messages = null;
        });
    };
    
    $scope.filterKeys = function () {
    	$http.post(actionUrl + "searchValue", $scope.searchValue).success(function (data) {
			$scope.checkData(data);

			$scope.selectedKey = null;
			$scope.messages = null;
			$scope.keys = data;
	    });
    };
    
    // fill the dropdown with the list of customers
    $scope.loadCustomers();
});