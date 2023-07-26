$(document).ready(function () {
    let totalPages = 1;
    let sort = "name"
    let dir = "asc";
    let sortId = $('#sortId');
    let sortName = $('#sortName');
    let sortPrice = $('#sortPrice');
    let table = $('#table tbody');
    let createProductButton = $('#createProductButton');
    let keyword = "";
    let searchButton = $('#searchButton');
    let resetButton = $('#resetButton');
    let inputKeyword = $('#keyword');

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

    searchButton.click(function () {
        keyword = inputKeyword.val();
        fetchProduct(0);
        inputKeyword.val(keyword);
    });

    resetButton.click(function () {
        keyword = "";
        inputKeyword.val(keyword);
        fetchProduct(0);
    });

    function fetchProduct(startPage) {

        $.ajax({
            type: "GET",
            url: "/products/search",
            data: {
                page: startPage,
                size: 10,
                sort: sort + "," + dir,
                keyword: keyword
            },
            success: function (response) {
                table.empty();
                $.each(response.content, (i, product) => {
                    let productId = product.id;
                    let productRow = '<tr>' +
                        '<td><div class="text-left">' + productId + '</div></td>' +
                        '<td><img alt="img"  height="60" id="thumbnail" width="60" src="http://images.example.com/products/' + product.image
                        + '" onerror="this.onerror=null;this.src=\'http://images.example.com/products/default.png\'" ' + '/></td>' +
                        '<td>' + product.name + '</td>' +
                        '<td>' + product.price + '</td>' +
                        '<td> ' +
                        '<a type="button" title="edit product" class="editProduct">' +
                        '<i class="material-icons admin">&#xe3c9;</i></a>&emsp;&emsp;' +
                        '<a type="button" title="delete product" class="deleteProduct">' +
                        '<i class="material-icons">&#xE872;</i></a></td> ' +
                        '</tr>';
                    $('#table tbody').append(productRow);
                });

                table.on('click', '.editProduct', function (e) {
                    let productId = $(this).closest('tr').find('.text-left').text();
                    editProduct(productId);
                    e.stopImmediatePropagation();
                });
                table.on('click', '.deleteProduct', function (e) {
                    let productId = $(this).closest('tr').find('.text-left').text();
                    deleteProduct(productId);
                    e.stopImmediatePropagation();
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

        let pageNumber = response.pageable.pageNumber;

        let numLinks = 10;

        let first = '';
        let prev = '';

        if (pageNumber > 0) {
            first = '<li class="page-item"><a class="page-link">«</a></li>';
            prev = '<li class="page-item"><a class="page-link">‹</a></li>';
        } else {
            prev = '';
            first = '';
        }

        let next = '';
        let last = '';
        if (pageNumber < totalPages) {
            if (pageNumber !== totalPages - 1) {
                next = '<li class="page-item"><a class="page-link">›</a></li>';
                last = '<li class="page-item"><a class="page-link">»</a></li>';
            }
        } else {
            next = '';
            last = '';
        }

        let start = pageNumber - (pageNumber % numLinks) + 1;
        let end = start + numLinks - 1;
        end = Math.min(totalPages, end);
        let pagingLink = '';

        for (let i = start; i <= end; i++) {
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
        let active = $("li.active");
        console.log('val: ' + val);

        let startPage;
        if (val.toUpperCase() === "«") {
            let currentActive = active;
            fetchProduct(0);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "»") {
            fetchProduct(totalPages - 1);
            let currentActive = active;
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