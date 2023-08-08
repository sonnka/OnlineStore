$(document).ready(function () {
    let totalPages = 1;
    let sort = "name.keyword"
    let dir = "asc";
    let sortId = $('#sortId');
    let sortName = $('#sortName');
    let sortPrice = $('#sortPrice');
    let keyword = "";
    let searchButton = $('#searchButton');
    let resetButton = $('#resetButton');
    let inputKeyword = $('#keyword');
    let table = $('#table tbody');

    let minDateField = $('#minDateFilter');
    let maxDateField = $('#maxDateFilter');
    let minPriceField = $('#minPriceFilter');
    let maxPriceField = $('#maxPriceFilter');
    let ratingField = $('#textFilter');

    let minDate = "";
    let maxDate = "";
    let minPrice = "";
    let maxPrice = "";
    let rating = "";

    fetchProductSearch(0, keyword);

    sortId.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "id.keyword";
        fetchProductSearch(0, keyword);
    });

    sortName.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "name.keyword";
        fetchProductSearch(0, keyword);
    });
    sortPrice.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "price";
        fetchProductSearch(0, keyword);
    });

    searchButton.click(function () {
        keyword = inputKeyword.val();
        minDate = minDateField.val();
        maxDate = maxDateField.val();
        minPrice = minPriceField.val();
        maxPrice = maxPriceField.val();
        rating = ratingField.val();
        fetchProductSearch(0, keyword);
        inputKeyword.val(keyword);
    });

    resetButton.click(function () {
        keyword = "";
        minDate = "";
        maxDate = "";
        minPrice = "";
        maxPrice = "";
        rating = "";

        inputKeyword.val(keyword);
        minDateField.val(minDate);
        maxDateField.val(maxDate);
        minPriceField.val(minPrice);
        maxPriceField.val(maxPrice);
        ratingField.val(rating);

        dir = "asc";
        sort = "name.keyword"

        fetchProductSearch(0);
    });


    function fetchProductSearch(startPage) {
        $.ajax({
            type: "GET",
            url: "/products/search",
            data: {
                page: startPage,
                size: 10,
                sort: sort + "," + dir,
                keyword: keyword,
                rating: rating,
                from: minPrice,
                to: maxPrice,
                dateFrom: minDate,
                dateTo: maxDate
            },
            success: function (response) {
                table.empty();
                $.each(response.content, (i, product) => {
                    let productId = product.id;
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

                table.on('click', '.addProduct', function (e) {
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
            fetchProductSearch(0);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "»") {
            fetchProductSearch(totalPages - 1);
            let currentActive = active;
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "›") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue < totalPages) {
                let currentActive = active;
                startPage = activeValue;
                fetchProductSearch(startPage);
                active.removeClass("active");
                currentActive.next().addClass("active");
            }
        } else if (val.toUpperCase() === "‹") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue > 1) {
                startPage = activeValue - 2;
                fetchProductSearch(startPage);
                let currentActive = active;
                currentActive.removeClass("active");
                currentActive.prev().addClass("active");
            }
        } else {
            startPage = parseInt(val - 1);
            fetchProductSearch(startPage);
            active.removeClass("active");
            $(this).parent().addClass("active");
        }
    });
});