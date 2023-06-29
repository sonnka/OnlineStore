$(document).ready(function () {
    var productId = $('#productId').val();

    var name = $('#name');
    var price = $('#price');
    var image = $('#thumbnail');
    var saveProductButton = $('#saveProductButton');

    var productImage = "default.png";

    if (productId === "-1") {
        $('#img').attr('style', "display:none");
        saveProductButton.click(function () {
            createProduct();
        });
    } else {
        loadProduct();
        $('#img').attr('style', "");
        saveProductButton.click(function () {
            updateProduct();
        });
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
        jsonData = {
            "name": name.val(),
            "price": price.val(),
            "image": productImage
        };

        $.ajax({
            type: "POST",
            url: "/admin/products",
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function (response) {
                productId = response.id;
                window.location = "/admin/products/" + productId + "/edit";
            }
        }).fail(function () {
            window.location = "/products_html";
        });
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
        image.attr('src', "http://images.example.com/products/default.png");
        image.attr('onerror', "this.onerror=null;this.src='http://images.example.com/products/default.png'");
        image.attr('src', "http://images.example.com/products/" + responseJson.image);
        productImage = responseJson.image;
    }

    function updateProduct() {
        jsonData = {
            "name": name.val(),
            "price": price.val(),
            "image": productImage
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