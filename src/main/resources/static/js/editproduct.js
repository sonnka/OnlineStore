$(document).ready(function () {
    var productId = $('#productId').val();

    var name = $('#name');
    var price = $('#price');
    var image = $('#thumbnail');

    if (productId === "-1") {
        createProduct();
    } else {
        loadProduct();
    }


    $('#imageForm').submit(function (event) {
        event.preventDefault();
        var formData = new FormData();
        formData.append('file', $('#imageFile')[0].files[0]);
        $.ajax({
            type: 'POST',
            url: '/admin/products/' + productId + '/upload',
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                loadProduct();
            },
            error: function (error) {
                loadProduct();
            }
        });
    });


    function createProduct() {

    }

    function loadProduct() {
        url = "/admin/products/" + productId;

        $.get(url, function (responseJson) {
            displayData(responseJson)
        }).fail(function () {
            window.location = "/";
        });
    }

    function displayData(responseJson) {
        name.val(responseJson.name);
        price.val(responseJson.price);
        image.attr('onerror', "this.onerror=null;this.src='http://images.example.com/products/default.png'");
        image.attr('src', "http://images.example.com/products/" + responseJson.image);
    }

    function updateProduct() {
        jsonData = {
            "id": productId,
            "name": name.val(),
            "image": image.val(),
            "price": price.val(),
        };

        $.ajax({
            type: "PATCH",
            url: "/admin/products/" + productId,
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function () {
                window.location = "/products_html";
            }
        }).fail(function () {
            window.location = "/products_html";
        });
    }

});