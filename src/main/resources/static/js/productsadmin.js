$(document).ready(function () {
    let totalPages = 1;
    let sort = "name"
    let dir = "asc";
    var sortId = $('#sortId');
    var sortName = $('#sortName');
    var sortPrice = $('#sortPrice');
    var table = $('#table tbody');
    var createProductButton = $('#createProductButton');

    fetchProduct(0);

    sortId.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "id";
        fetchProduct(0);
    });

    sortName.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "name";
        fetchProduct(0);
    });
    sortPrice.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "price";
        fetchProduct(0);
    });

    createProductButton.click(function () {
        createProduct();
    });

    function fetchProduct(startPage) {

        $.ajax({
            type: "GET",
            url: "/products",
            data: {
                page: startPage,
                size: 7,
                sort: sort + "," + dir
            },
            success: function (response) {
                table.empty();
                $.each(response.content, (i, product) => {
                    var productId = product.id;
                    let productRow = '<tr>' +
                        '<td><div class="text-left">' + productId + '</div></td>' +
                        '<td><img alt="img"  height="70" id="thumbnail" width="70" src="http://images.example.com/products/' + product.image
                        + '" onerror="this.onerror=null;this.src=\'http://images.example.com/products/default.png\'" ' + '/></td>' +
                        '<td>' + product.name + '</td>' +
                        '<td>' + product.price + '</td>' +
                        '<td> ' +
                        '<a type="button" title="edit product" class="editProduct">' +
                        '<i class="material-icons admin">&#xe3c9;</i></a>&emsp;&emsp;' +
                        '<a type="button" title="delete product" class="deleteProduct">' +
                        '<i class="material-icons">&#xE872;</i></a></div></td> ' +
                        '</tr>';
                    $('#table tbody').append(productRow);
                });

                table.on('click', '.editProduct', function () {
                    let productId = $(this).closest('tr').find('.text-left').text();
                    editProduct(productId);
                });
                table.on('click', '.deleteProduct', function () {
                    let productId = $(this).closest('tr').find('.text-left').text();
                    deleteProduct(productId);
                });

                if ($('ul.pagination li').length - 2 !== response.totalPages) {
                    $('ul.pagination').empty();
                    buildPagination(response);
                }
            },
            error: function (e) {
                console.log("ERROR: ", e);
                window.location = "/";
            }
        });
    }

    function createProduct() {
        window.location = "/admin/products/create";
    }

    function editProduct(productId) {
        window.location = "/admin/products/" + productId + "/edit";
    }

    function deleteProduct(productId) {
        $.ajax({
            type: "DELETE",
            url: "/admin/products/" + productId,
            success: function () {
                window.location = "/products_html?success";
            }
        }).fail(function () {
            window.location = "/products_html?error";
        });
    }

    function buildPagination(response) {
        totalPages = response.totalPages;

        var pageNumber = response.pageable.pageNumber;

        var numLinks = 10;

        var first = '';
        var prev = '';

        if (pageNumber > 0) {
            first = '<li class="page-item"><a class="page-link">«</a></li>';
            prev = '<li class="page-item"><a class="page-link">‹</a></li>';
        } else {
            prev = '';
            first = '';
        }

        var next = '';
        var last = '';
        if (pageNumber < totalPages) {
            if (pageNumber !== totalPages - 1) {
                next = '<li class="page-item"><a class="page-link">›</a></li>';
                last = '<li class="page-item"><a class="page-link">»</a></li>';
            }
        } else {
            next = '';
            last = '';
        }

        var start = pageNumber - (pageNumber % numLinks) + 1;
        var end = start + numLinks - 1;
        end = Math.min(totalPages, end);
        var pagingLink = '';

        for (var i = start; i <= end; i++) {
            if (i === (pageNumber + 1)) {
                pagingLink += '<li class="page-item active"><a class="page-link"> ' + i + ' </a></li>';
            } else {
                pagingLink += '<li class="page-item"><a class="page-link"> ' + i + ' </a></li>';
            }
        }

        pagingLink = first + prev + pagingLink + next + last;

        $("ul.pagination").append(pagingLink);
    }

    $(document).on("click", "ul.pagination li a", function () {
        let val = $(this).text();
        var active = $("li.active");
        console.log('val: ' + val);

        if (val.toUpperCase() === "«") {
            let currentActive = active;
            fetchProduct(0);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "»") {
            fetchProduct(totalPages - 1);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "›") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue < totalPages) {
                let currentActive = active;
                startPage = activeValue;
                fetchProduct(startPage);
                active.removeClass("active");
                currentActive.next().addClass("active");
            }
        } else if (val.toUpperCase() === "‹") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue > 1) {
                startPage = activeValue - 2;
                fetchProduct(startPage);
                let currentActive = active;
                currentActive.removeClass("active");
                currentActive.prev().addClass("active");
            }
        } else {
            startPage = parseInt(val - 1);
            fetchProduct(startPage);
            active.removeClass("active");
            $(this).parent().addClass("active");
        }
    });
});