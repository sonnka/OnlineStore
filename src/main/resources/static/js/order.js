$(document).ready(function () {
    var editOrderButton = $('#editOrderButton');
    var deleteOrderButton = $('#deleteOrderButton');

    var customerId = $('#customerId').val();
    var orderId = $('#orderId').val();

    loadOrder()

    editOrderButton.click(function () {
        window.location = "/profile/orders/" + orderId + "/edit";
    });

    deleteOrderButton.click(function () {
        deleteOrder();
    });

    function loadOrder() {
        url = "/customers/" + customerId + "/orders/" + orderId;

        $.get(url, function (responseJson) {
            displayData(responseJson)
        }).fail(function () {
            window.location = "/";
        });
    }

    function displayData(responseJson) {
        $('#orderDate').text(responseJson.date);
        $('#orderDeliveryAddress').text(responseJson.deliveryAddress);
        $('#orderDescription').text(responseJson.description);
        $('#orderStatus').text(responseJson.status);
        $('#orderPrice').text(responseJson.price);

        $('#productsTable tbody').empty();
        $.each(responseJson.products, (i, product) => {
            let productRow = '<tr>' +
                '<td>' + product.name + '</td>' +
                '<td>' + product.price + '</td>' +
                '<td>' + product.count + '</td>' +
                '</tr>';
            $('#productsTable tbody').append(productRow);
        });
    }

    function deleteOrder() {
        $.ajax({
            type: "DELETE",
            url: "/customers/" + customerId + "/orders/" + orderId,
            success: function () {
                window.location = "/profile/orders";
            },
            error: function () {
                window.location = "/profile/orders";
            }
        })
    }
});