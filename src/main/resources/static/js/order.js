$(document).ready(function () {
    let editOrderButton = $('#editOrderButton');
    let deleteOrderButton = $('#deleteOrderButton');
    let customerId = $('#customerId').val();
    let orderId = $('#orderId').val();
    let orderStatus = "UNPAID";
    let status = $('#payStatus').val();

    loadOrder()

    editOrderButton.click(function () {
        window.location = "/profile/orders/" + orderId + "/edit";
    });

    deleteOrderButton.click(function () {
        deleteOrder();
    });

    function loadOrder() {
        let url = "/customers/" + customerId + "/orders/" + orderId;

        $.get(url, function (responseJson) {
            orderStatus = responseJson.status;
            if (status === "succeeded") {
                if (responseJson.status !== "PAID") {
                    updateStatus();
                }
            }
            displayData(responseJson);
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

        let form = document.getElementById('checkout-form');

        if (orderStatus === "PAID") {
            $('#editbutton').attr("type", "hidden");
            $('#deletebutton').attr("type", "submit");
            form.style.display = 'none';
        } else {
            $('#deletebutton').attr("type", "hidden");
            $('#editbutton').attr("type", "submit")
            form.style.display = '';
        }
    }

    function deleteOrder() {
        $.ajax({
            type: "DELETE",
            url: "/customers/" + customerId + "/orders/" + orderId,
            success: function () {
                window.location = "/profile/orders";
            },
            error: function () {
                if (orderStatus === "UNPAID") {
                    window.location = "/profile/orders/" + orderId + "?error_unpaid";
                } else {
                    window.location = "/profile/orders/" + orderId + "?error";
                }
            }
        })
    }

    function updateStatus() {
        let jsonData = {
            "status": "PAID"
        };

        $.ajax({
            type: "PATCH",
            url: "/customers/" + customerId + "/orders/" + orderId,
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function () {
                loadOrder();
            }
        }).fail(function () {
            window.location = "/";
        });
    }
});