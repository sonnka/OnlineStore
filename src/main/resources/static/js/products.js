$(document).ready(function () {
    let totalPages = 1;
    let sort = "name"
    let dir = "asc";
    var sortId = $('#sortId');
    var sortName = $('#sortName');
    var sortPrice = $('#sortPrice');
    var keyword = "";
    var searchButton = $('#searchButton');
    var resetButton = $('#resetButton');
    var inputKeyword = $('#keyword');

    fetchProductSearch(0, keyword);

    sortId.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "id";
        fetchProductSearch(0, keyword);
    });

    sortName.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "name";
        fetchProductSearch(0, keyword);
    });
    sortPrice.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "price";
        fetchProductSearch(0, keyword);
    });

    searchButton.click(function () {
        keyword = inputKeyword.val();
        fetchProductSearch(0, keyword);
        inputKeyword.val(keyword);
    });

    resetButton.click(function () {
        keyword = "";
        inputKeyword.val(keyword);
        fetchProductSearch(0, keyword);
    });


    function fetchProductSearch(startPage, keyword) {

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
                $('#table tbody').empty();
                $.each(response.content, (i, product) => {
                    var productId = product.id;
                    let productRow = '<tr>' +
                        '<td style="display:none;" ><div class="text-left">' + productId + '</div></td>' +
                        '<td><img alt="img" height="60" id="thumbnail" width="60" ' +
                        'src="http://images.example.com/products/' + product.image +
                        '" onerror="this.onerror=null;this.src=\'http://images.example.com/products/default.png\'" ' +
                        '/></td>' +
                        '<td>' + product.name + '</td>' +
                        '<td>' + product.price + '</td>' +
                        '<td><a type="button" title="add product" class="addProduct">' +
                        '<i class="material-icons admin">&#xe145;</i></a></td>' +
                        '</tr>';
                    $('#table tbody').append(productRow);
                });

                $('#columnId').attr("style", "display:none");

                $('#table tbody').on('click', '.addProduct', function (e) {
                    let productId = $(this).closest('tr').find('.text-left').text();
                    addProductToBasket(productId);
                    e.stopImmediatePropagation();
                });

                if ($('ul.pagination li').length - 2 !== response.totalPages) {
                    $('ul.pagination').empty();
                    buildSearchPagination(response);
                }
            },
            error: function (e) {
                console.log("ERROR: ", e);
                window.location = "/";
            }
        });
    }

    function addProductToBasket(productId) {
        $.ajax({
            type: "PATCH",
            url: "/basket/" + productId,
            contentType: 'application/json',
            success: function () {

            }
        }).fail(function () {
            window.location = "/products_html";
        });

    }

    function buildSearchPagination(response) {
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
        var data = $(this).attr('data');
        let val = $(this).text();
        var active = $("li.active");
        console.log('val: ' + val);

        if (val.toUpperCase() === "«") {
            let currentActive = active;
            fetchProductSearch(0, keyword);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "»") {
            fetchProductSearch(totalPages - 1, keyword);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "›") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue < totalPages) {
                let currentActive = active;
                startPage = activeValue;
                fetchProductSearch(startPage, keyword);
                active.removeClass("active");
                currentActive.next().addClass("active");
            }
        } else if (val.toUpperCase() === "‹") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue > 1) {
                startPage = activeValue - 2;
                fetchProductSearch(startPage, keyword);
                let currentActive = active;
                currentActive.removeClass("active");
                currentActive.prev().addClass("active");
            }
        } else {
            startPage = parseInt(val - 1);
            fetchProductSearch(startPage, keyword);
            active.removeClass("active");
            $(this).parent().addClass("active");
        }
    });
});