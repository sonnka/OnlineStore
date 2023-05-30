$(document).ready(function () {
    var customerId = $('#customerId').val();
    var orderId = $('#orderId').val();

    var date = $('#date');
    var deliveryAddress = $('#deliveryAddress');
    var description = $('#description');
    var price = $('#price');

    loadOrder();

    var saveButton = $('#saveButton');
    var addProductButton = $('#addProductButton');
    var addSelectedProducts = $('#addSelectedProducts');

    saveButton.click(function () {

    });

    addProductButton.click(function () {

    });

    addSelectedProducts.click(function () {

    });

    function loadOrder() {
        url = "/customers/" + customerId + "/orders/" + orderId;

        $.get(url, function (responseJson) {
            displayData(responseJson)
        }).fail(function () {
            window.location = "/profile/orders/" + orderId;
        });
    }

    function displayData(responseJson) {
        date.val(responseJson.date);
        deliveryAddress.val(responseJson.deliveryAddress);
        description.val(responseJson.description);
        price.text(responseJson.price);

        $('#productsTable tbody').empty();
        $.each(responseJson.products, (i, product) => {
            let productRow = '<tr>' +
                '<td data-th="Product"><div class="col-md-9 text-left mt-sm-2">' + product.name + '</div></td>' +
                '<td class="valueAlign" data-th="Price">' + product.price + '</td>' +
                '<td data-th="Quantity"><input class="form-control form-control-lg text-center input_count" min="1"'
                + ' style="height: 25px" value="' + product.count + '" type="number"/></td>' +
                '<td class="actions" data-th=""> <div class="text-right valueAlign">' +
                '<a class="delete" data-toggle="tooltip" th:href="@{/customers/' + customerId
                + '/orders/' + orderId + '}" title="Delete">' +
                '<i class="material-icons">&#xE872;</i></a></div></td> ' +
                '</tr>';
            $('#productsTable tbody').append(productRow);
        });

    }
});