let productsTable;
$(document).ready(function () {
    productsTable = $("#productsTable");
    loadProducts();
    pagination();


    function loadProducts() {
        url = "/products";

        $.get(url, function (responseJson) {
            productsTable.empty();
            var prod = responseJson.products;
            $.each(prod, function (index, p) {
                productsTable.append("<tr><td>" + p.name + "</td><td>" + p.price + "</td></tr>");
            });
        }).done(function () {
            //  alert("Done")
        }).fail(function () {
            //     alert("Fail");
        });
    }

    function pagination() {
        $('#table').DataTable({
            pagingType: 'numbers',
        });
    }
});