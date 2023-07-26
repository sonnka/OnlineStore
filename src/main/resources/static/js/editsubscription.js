$(document).ready(function () {
    let subscriptionId = $('#subscriptionId').val();
    let image = $('#image');
    let name = $('#name');
    let description = $('#description');
    let price = $('#price');
    let currency = $('#currency');
    let recurring = $('#recurring');
    let saveSubButton = $('#saveSubButton');


    if (subscriptionId === "-1") {
        saveSubButton.click(function () {
            createSub();
        });
    } else {
        loadSub();
        saveSubButton.click(function () {
            updateSub();
        });
    }

    function createSub() {
        let jsonData = {
            "image": image.val(),
            "name": name.val(),
            "price": price.val(),
            "description": description.val(),
            "currency": currency.val(),
            "recurring": recurring.val()
        };

        $.ajax({
            type: "POST",
            url: "/stripe/products",
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function (response) {
                subscriptionId = response.id;
                window.location = "/subscriptions";
            }
        }).fail(function () {
            window.location = "/subscriptions";
        });
    }

    function loadSub() {
        let url = "/stripe/products/" + subscriptionId;

        $.get(url, function (responseJson) {
            displayData(responseJson)
        }).fail(function () {
            window.location = "/subscriptions";
        });
    }

    function displayData(responseJson) {
        name.val(responseJson.name);
        price.val(responseJson.price);
        image.val(responseJson.image);
        description.val(responseJson.description);
        recurring.val(responseJson.recurring);
        currency.val(responseJson.currency);
    }

    function updateSub() {
        let jsonData = {
            "image": image.val(),
            "name": name.val(),
            "price": price.val(),
            "description": description.val(),
            "currency": currency.val(),
            "recurring": recurring.val()
        };

        $.ajax({
            type: "PATCH",
            url: "/stripe/products/" + subscriptionId,
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function () {
                window.location = "/subscriptions";
            }
        }).fail(function () {
            window.location = "/subscriptions";
        });
    }


});