let listOfProducts = [];
$(document).ready(function () {
    let customerId = $('#customerId').val();
    let orderId = $('#orderId').val();

    let date = $('#date');
    let status = $("#orderStatus");
    let deliveryAddress = $('#deliveryAddress');
    let description = $('#description');
    let price = $('#price');
    let table = $('#productsTable tbody');
    let type = "DRAFT";
    let saveButton = $('#saveButton');
    let count = 0;

    loadOrder();

    function loadOrder() {
        let url = "/customers/" + customerId + "/orders/" + orderId;

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

        count = 0;

        table.empty();
        $.each(responseJson.products, (i, product) => {
            let productName = product.name;
            count++;
            let productRow = '<tr>' +
                '<td style="display:none;"  data-th="Id"><div class="col-md-9 text-left mt-sm-2">' + product.id + '</div></td>' +
                '<td data-th="Product"><div class="col-md-9 mt-sm-2">' + productName + '</div></td>' +
                '<td class="valueAlign" data-th="Price">' + product.price + '</td>' +
                '<td data-th="Quantity"><input class="form-control form-control-lg text-center input_count" min="1"'
                + ' style="height: 25px" value="' + product.count + '" type="number"/></td>' +
                '<td class="actions" data-th="" id="deleteColumn"> <div class="text-right valueAlign">' +
                '<a type="button" title="delete" class="deleteProduct">' +
                '<i class="material-icons">&#xE872;</i></a></div></td> ' +
                '</tr>';
            table.append(productRow);
        });

        let button = document.getElementById('deleteColumn');

        if (count <= 1) {
            button.style.display = 'none';
        } else {
            button.style.display = '';
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
        let jsonData = {
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
        table.find('tr').each(function () {
            let row = $(this);

            let productId = row.find('td[data-th="Id"] .col-md-9').text().trim();
            let productName = row.find('td[data-th="Product"] .col-md-9').text().trim();
            let productCount = Number(row.find('td[data-th="Quantity"] .input_count').val().trim());

            let rowData = {
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
                let jsonData = {
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
                        window.location = "/profile/orders/" + orderId;
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
                window.location = "/profile/orders/" + orderId;
            });
        }
    }
});