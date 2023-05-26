$(document).ready(function () {
    let totalPages = 1;
    let sort = "name"
    let dir = "asc";
    var sortName = $('#sortName');
    var sortPrice = $('#sortPrice');

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

    function fetchProduct(startPage) {

        $.ajax({
            type: "GET",
            url: "http://localhost:8080/products",
            data: {
                page: startPage,
                size: 10,
                sort: sort + "," + dir
            },
            success: function (response) {
                $('#table tbody').empty();
                $.each(response.content, (i, product) => {
                    let productRow = '<tr>' +
                        '<td>' + product.name + '</td>' +
                        '<td>' + product.price + '</td>' +
                        '</tr>';
                    $('#table tbody').append(productRow);
                });

                if ($('ul.pagination li').length - 2 != response.totalPages) {
                    $('ul.pagination').empty();
                    buildPagination(response);
                }
            },
            error: function (e) {
                alert("ERROR: ", e);
                console.log("ERROR: ", e);
            }
        });
    }

    function buildPagination(response) {
        totalPages = response.totalPages;

        var pageNumber = response.pageable.pageNumber;

        var numLinks = 10;

        var first = '';
        var prev = '';
        if (pageNumber > 0) {
            if (pageNumber !== 0) {
                first = '<li class="page-item"><a class="page-link">« First</a></li>';
            }
            prev = '<li class="page-item"><a class="page-link">‹ Prev</a></li>';
        } else {
            prev = '';
            first = '';
        }

        var next = '';
        var last = '';
        if (pageNumber < totalPages) {
            if (pageNumber !== totalPages - 1) {
                next = '<li class="page-item"><a class="page-link">Next ›</a></li>';
                last = '<li class="page-item"><a class="page-link">Last »</a></li>';
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
            if (i == (pageNumber + 1)) {
                pagingLink += '<li class="page-item active"><a class="page-link"> ' + i + ' </a></li>'; // no need to create a link to current page
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
        console.log('val: ' + val);

        if (val.toUpperCase() === "« FIRST") {
            let currentActive = $("li.active");
            fetchProduct(0);
            $("li.active").removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "LAST »") {
            fetchProduct(totalPages - 1);
            $("li.active").removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "NEXT ›") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue < totalPages) {
                let currentActive = $("li.active");
                startPage = activeValue;
                fetchProduct(startPage);
                $("li.active").removeClass("active");
                currentActive.next().addClass("active");
            }
        } else if (val.toUpperCase() === "‹ PREV") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue > 1) {
                startPage = activeValue - 2;
                fetchProduct(startPage);
                let currentActive = $("li.active");
                currentActive.removeClass("active");
                currentActive.prev().addClass("active");
            }
        } else {
            startPage = parseInt(val - 1);
            fetchProduct(startPage);
            $("li.active").removeClass("active");
            $(this).parent().addClass("active");
        }
    });

    (function () {
        fetchProduct(0);
    })();
});