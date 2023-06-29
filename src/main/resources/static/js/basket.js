var listOfProducts = [];
$(document).ready(function () {
    var customerId = $('#customerId').val();
    var basketId = $('#basketId').val();

    var price = $('#price');
    var table = $('#productsTable tbody');
    var publishButton = $('#publishButton')


    loadBasket();


    publishButton.click(function () {
        jsonData = {
            "products": listOfProducts
        };

        $.ajax({
            type: "PATCH",
            url: "/customers/" + customerId + "/orders/" + basketId,
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function () {
                window.location = "/profile/orders/" + basketId + "/edit";
            }
        }).fail(function () {
            window.location = "/profile/basket?error";
        });
    });


    function loadBasket() {
        url = "/customers/" + customerId + "/orders/" + basketId;
        $.get(url, function (responseJson) {
            displayBasketData(responseJson)
        }).fail(function () {
            window.location = "/";
        });
    }

    function displayBasketData(responseJson) {
        price.text(responseJson.price);

        table.empty();

        $.each(responseJson.products, (i, product) => {
            var productName = product.name;

            let productRow = '<tr>' +
                '<td data-th="Id"><div class="col-md-9 text-left mt-sm-2">' + product.id + '</div></td>' +
                '<td data-th="Product"><div class="col-md-9 mt-sm-2">' + productName + '</div></td>' +
                '<td class="valueAlign" data-th="Price">' + product.price + '</td>' +
                '<td data-th="Quantity"><input class="form-control form-control-lg text-center input_count" min="1"'
                + ' style="height: 25px" value="' + product.count + '" type="number"/></td>' +
                '<td class="actions" data-th=""> <div class="text-right valueAlign">' +
                '<a type="button" title="delete" class="deleteProduct">' +
                '<i class="material-icons" >&#xE872;</i></a></div></td> ' +
                '</tr>';
            table.append(productRow);
        });
        table.on('change', '.input_count', function (e) {
            listOfProducts = [];
            updateList();
            updateBasket();
            e.stopImmediatePropagation();
        });
        table.on('click', '.deleteProduct', function (e) {
            let productId = $(this).closest('tr').find('.text-left').text();
            deleteProduct(productId);
            e.stopImmediatePropagation();
        });
    }


    function updateBasket() {
        jsonData = {
            "products": listOfProducts
        };

        $.ajax({
            type: "PATCH",
            url: "/customers/" + customerId + "/orders/" + basketId,
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function (responseJson) {
                displayBasketData(responseJson);
            }
        }).fail(function () {
            window.location = "/profile/basket?error";
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
        updateBasket();
    }

});