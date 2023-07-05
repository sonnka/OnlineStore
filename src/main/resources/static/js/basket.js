let listOfProducts = [];
$(document).ready(function () {
    let customerId = $('#customerId').val();
    let basketId = $('#basketId').val();

    let price = $('#price');
    let table = $('#productsTable tbody');
    let publishButton = $('#publishButton');
    let count = 0;

    loadBasket();

    publishButton.click(function () {
        window.location = "/profile/orders/" + basketId + "/edit";
    });

    function loadBasket() {
        let url = "/customers/" + customerId + "/orders/" + basketId;
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
            let productName = product.name;
            ++count;
            let productRow = '<tr>' +
                '<td style="display:none;"  data-th="Id"><div class="col-md-9 text-left mt-sm-2">'
                + product.id + '</div></td>' +
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

        if (count <= 0) {
            publishButton.attr("style", "display:none");
        } else {
            publishButton.attr("style", "");
        }
    }

    function updateBasket() {
        let jsonData = {
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
        updateBasket();
    }
});