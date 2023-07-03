var listOfProducts = [];
$(document).ready(function () {
    var customerId = $('#customerId').val();
    var orderId = $('#orderId').val();

    var date = $('#date');
    var status = $("#orderStatus");
    var deliveryAddress = $('#deliveryAddress');
    var description = $('#description');
    var price = $('#price');
    var table = $('#productsTable tbody');
    var type = "DRAFT";
    var saveButton = $('#saveButton');
    var count = 0;


    loadOrder();

    function loadOrder() {
        url = "/customers/" + customerId + "/orders/" + orderId;

        $.get(url, function (responseJson) {
            type = responseJson.type;
            checkType();
            displayData(responseJson);
        }).fail(function () {
            window.location = "/profile/orders/" + orderId;
        });
    }

    function displayData(responseJson) {
        date.val(responseJson.date);
        status.text(responseJson.status);
        deliveryAddress.val(responseJson.deliveryAddress);
        description.val(responseJson.description);
        price.text(responseJson.price);

        table.empty();
        $.each(responseJson.products, (i, product) => {
            var productName = product.name;
            ++count;
            let productRow = '<tr>' +
                '<td style="display:none;"  data-th="Id"><div class="col-md-9 text-left mt-sm-2">' + product.id + '</div></td>' +
                '<td data-th="Product"><div class="col-md-9 mt-sm-2">' + productName + '</div></td>' +
                '<td class="valueAlign" data-th="Price">' + product.price + '</td>' +
                '<td data-th="Quantity"><input class="form-control form-control-lg text-center input_count" min="1"'
                + ' style="height: 25px" value="' + product.count + '" type="number"/></td>' +
                '<td class="actions" data-th=""> <div class="text-right valueAlign">' +
                '<a type="button" title="delete" class="deleteProduct">' +
                '<i class="material-icons">&#xE872;</i></a></div></td> ' +
                '</tr>';
            table.append(productRow);
        });

        if (count <= 0) {
            saveButton.attr("style", "display:none");
            loadOrder();
        } else {
            saveButton.attr("style", "");
        }

        table.on('change', '.input_count', function (e) {
            listOfProducts = [];
            updateList();
            updateOrder();
            e.stopImmediatePropagation();
        });
        table.on('click', '.deleteProduct', function (e) {
            let productId = $(this).closest('tr').find('.text-left').text();
            deleteProduct(productId);
            e.stopImmediatePropagation();
        });
    }

    function updateOrder() {
        jsonData = {
            "products": listOfProducts,
            "deliveryAddress": deliveryAddress.val(),
            "description": description.val()
        };

        $.ajax({
            type: "PATCH",
            url: "/customers/" + customerId + "/orders/" + orderId,
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function (responseJson) {
                displayData(responseJson);
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

            var productId = row.find('td[data-th="Id"] .col-md-9').text().trim();
            var productName = row.find('td[data-th="Product"] .col-md-9').text().trim();
            var productCount = Number(row.find('td[data-th="Quantity"] .input_count').val().trim());

            var rowData = {
                "id": productId,
                "name": productName,
                "count": productCount
            };

            listOfProducts.push(rowData);

        });
    }

    function deleteProduct(productId) {
        updateList();
        listOfProducts = listOfProducts.filter(prod => prod.id !== productId);
        updateOrder();
    }

    function checkType() {
        if (type === "DRAFT") {
            saveButton.click(function () {
                listOfProducts = [];
                updateList();
                jsonData = {
                    "products": listOfProducts,
                    "deliveryAddress": deliveryAddress.val(),
                    "description": description.val()
                };

                $.ajax({
                    type: "POST",
                    url: "/customers/" + customerId + "/orders/" + orderId,
                    data: JSON.stringify(jsonData),
                    contentType: 'application/json',
                    success: function () {
                        window.location = "/profile/orders"
                    }
                }).fail(function () {
                    window.location = "/profile/orders/" + orderId + "/edit?error";
                });
            });
        } else {
            saveButton.click(function () {
                listOfProducts = [];
                updateList();
                updateOrder();
                window.location = "/profile/orders"
            });
        }
    }
});