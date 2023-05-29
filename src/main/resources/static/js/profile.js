$(document).ready(function () {
    var customerId = $('#customerId').val();
    loadProfile();
    var editProfileButton = $('#editProfileButton');
    var deleteProfileButton = $('#deleteProfileButton');
    var getOrdersButton = $('#getOrdersButton');

    editProfileButton.click(function () {
        window.location = "/profile/edit";
    });
    deleteProfileButton.click(function () {
        deleteProfile();
    });
    getOrdersButton.click(function () {
        window.location = "/profile/orders";
    });


    function loadProfile() {
        url = "/customers/" + customerId;

        $.get(url, function (responseJson) {
            displayData(responseJson)
        }).fail(function () {
            window.location = "/";
        });
    }

    function displayData(responseJson) {
        var customerNameSurname = $('#customerNameSurname');
        var customerName = $('#customerName');
        var customerSurname = $('#customerSurname');
        var customerEmail = $('#customerEmail');
        var customerEmail2 = $('#customerEmail2');
        var totalAmountOfOrders = $('#totalAmountOfOrders');
        var totalAmountOfOrders2 = $('#totalAmountOfOrders2');
        var amountPaidOrders = $('#amountPaidOrders');
        var amountUnpaidOrders = $('#amountUnpaidOrders');
        customerNameSurname.text(responseJson.name + " " + responseJson.surname);
        customerName.text(responseJson.name);
        customerSurname.text(responseJson.surname);
        customerEmail.text(responseJson.email);
        customerEmail2.text(responseJson.email);
        totalAmountOfOrders.text(responseJson.totalAmountOfOrders);
        totalAmountOfOrders2.text(responseJson.totalAmountOfOrders);
        amountPaidOrders.text(responseJson.amountOfPaidOrders);
        amountUnpaidOrders.text(responseJson.amountOfUnpaidOrders);
    }

    function deleteProfile() {
        $.ajax({
            type: "DELETE",
            url: "/customers/" + customerId,
            success: function () {
                logout();
            },
            error: function (e) {
                console.log(e);
                alert("fail");
            }
        })
    }

    function logout() {
        $.ajax({
            type: "POST",
            url: "/logout",
            contentType: "application/json",
            dataType: 'json',
            success: function () {
                window.location = "/";
            },
            error: function (e) {
                console.log(e);
                alert("fail");
            }
        })
    }

});