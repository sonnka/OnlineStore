var listOfProducts = [];
$(document).ready(function () {
    var customerId = $('#customerId').val();
    var orderId = $('#orderId').val();

    var date = $('#date');
    var status = $("#statuses");
    var deliveryAddress = $('#deliveryAddress');
    var description = $('#description');
    var price = $('#price');
    var table = $('#productsTable tbody');

    if (orderId === "-1") {
        createOrder();
    } else {
        loadOrder();
    }


    var saveButton = $('#saveButton');
    var addProductButton = $('#addProductButton');
    var addSelectedProducts = $('#addSelectedProducts');

    saveButton.click(function () {
        listOfProducts = [];
        updateList();
        updateOrder();
    });

    addProductButton.click(function () {
        $.get("/customers/" + customerId + "/orders/" + orderId + "/productList", function (responseJson) {
            displayList(responseJson)
        }).fail(function () {
            window.location = "/profile/orders/" + orderId;
        });
    });

    addSelectedProducts.click(function () {
        $('input[type="checkbox"]:checked').each(function () {
            var $row = $(this).closest('tr');
            var name = $row.find('label.name').text().trim();

            var selectedProduct = {
                "name": name,
                "count": 1
            }
            listOfProducts.push(selectedProduct);
        });
        updateList();
        updateOrder();
        window.location = "/profile/orders/" + orderId + "/edit";
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
        status.val(responseJson.status);
        deliveryAddress.val(responseJson.deliveryAddress);
        description.val(responseJson.description);
        price.text(responseJson.price);

        table.empty();
        $.each(responseJson.products, (i, product) => {
            var productName = product.name;

            let productRow = '<tr>' +
                '<td data-th="Product"><div class="col-md-9 text-left mt-sm-2">' + productName + '</div></td>' +
                '<td class="valueAlign" data-th="Price">' + product.price + '</td>' +
                '<td data-th="Quantity"><input class="form-control form-control-lg text-center input_count" min="1"'
                + ' style="height: 25px" value="' + product.count + '" type="number"/></td>' +
                '<td class="actions" data-th=""> <div class="text-right valueAlign">' +
                '<a type="button" title="delete" class="deleteProduct">' +
                '<i class="material-icons">&#xE872;</i></a></div></td> ' +
                '</tr>';
            table.append(productRow);
        });
        table.on('click', '.deleteProduct', function () {
            let productName = $(this).closest('tr').find('.text-left').text();
            deleteProduct(productName);
        });

    }

    function displayList(responseJson) {
        $('#productsList tbody').empty();
        $.each(responseJson, (i, product) => {
            let productRow = '<tr>' +
                '<td> <input type="checkbox" class="checkbox"> <label class="name">&emsp;&emsp;' + product.name
                + '&emsp;&emsp;</label></td>' +
                '<td> <label class="price">' + product.price + '</label></td></tr>'
            $('#productsList tbody').append(productRow);
        });
    }

    function updateOrder() {
        jsonData = {
            "status": status.val(),
            "products": listOfProducts,
            "deliveryAddress": deliveryAddress.val(),
            "description": description.val()
        };

        $.ajax({
            type: "PATCH",
            url: "/customers/" + customerId + "/orders/" + orderId,
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function () {
                window.location = "/profile/orders/" + orderId;
            }
        }).fail(function () {
            window.location = "/profile/orders/" + orderId + "/edit?error";
        });

        listOfProducts = [];
    }

    function updateList() {
        var tbody = $('#productsTable tbody');

        tbody.find('tr').each(function () {
            var row = $(this);

            var productName = row.find('td[data-th="Product"] .col-md-9').text().trim();
            var productCount = Number(row.find('td[data-th="Quantity"] .input_count').val().trim());

            var rowData = {
                "name": productName,
                "count": productCount
            };

            listOfProducts.push(rowData);

        });
    }

    function deleteProduct(productName) {
        updateList();
        listOfProducts = listOfProducts.filter(prod => prod.name !== productName);
        updateOrder();
        window.location = "/profile/orders/" + orderId + "/edit";
    }

    function createOrder() {
        jsonData = {
            "status": "UNPAID",
            "products": [],
            "deliveryAddress": "",
            "description": ""
        };

        $.ajax({
            type: "POST",
            url: "/customers/" + customerId + "/orders",
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function (responseJson) {
                orderId = responseJson.id;
                window.location = "/profile/orders/" + orderId + "/edit";
            }
        }).fail(function () {
            window.location = "/profile/orders";
        });
    }

});