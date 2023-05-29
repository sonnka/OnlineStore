$(document).ready(function () {
    var customerId = $('#customerId').val();
    var saveOrderButton = $('#saveOrderButton');

    var nameSurname = $('#nameSurname');
    var name = $('#name');
    var surname = $('#surname');
    var email = $('#email');
    var totalAmount = $('#totalAmount');

    loadProfile();

    saveOrderButton.click(function () {
        updateOrder();
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
        nameSurname.text(responseJson.name + " " + responseJson.surname);
        name.val(responseJson.name);
        surname.val(responseJson.surname);
        email.text(responseJson.email);
        totalAmount.text(responseJson.totalAmountOfOrders);
    }

    function updateOrder() {
        jsonData = {
            "id": customerId,
            "name": name.val(),
            "surname": surname.val(),
            "email": email.val(),
        };

        $.ajax({
            type: "PATCH",
            url: "/customers/" + customerId,
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function () {
                window.location = "/profile";
            }
        }).fail(function () {
            alert("fail");
            window.location = "/profile";
        });
    }

});