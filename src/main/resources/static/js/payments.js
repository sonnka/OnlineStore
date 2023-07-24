$(document).ready(function () {
    let totalPages = 1;
    let sort = "date"
    let dir = "desc";
    let sortDate = $('#sortDate');
    let table = $('#table tbody');
    let color = "#158116";

    fetchPayments(0);

    sortDate.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        fetchPayments(0);
    });

    function fetchPayments(startPage) {
        $.ajax({
            type: "GET",
            url: "/payments",
            data: {
                page: startPage,
                size: 7,
                sort: sort + "," + dir
            },
            success: function (response) {
                table.empty();
                $.each(response.content, (i, payment) => {

                    if (payment.paymentStatus !== "succeeded" && payment.paymentStatus !== "active") {
                        color = "#c21f1f";
                    } else {
                        color = "#158116";
                    }

                    let paymentRow = '<tr>' +
                        '<td style="color:' + color + '">' + payment.id + '</td>' +
                        '<td style="color:' + color + '">' + payment.date + '</td>' +
                        '<td style="color:' + color + '">' + payment.description + '</td>' +
                        '<td style="color:' + color + '">' + payment.price + '</td>' +
                        '<td style="color:' + color + '">' + payment.card + '</td>' +
                        '<td style="color:' + color + '">' + payment.paymentStatus + '</td>' +
                        '<td style="color:' + color + '">' + payment.errors + '</td>' +
                        '</tr>';
                    $('#table tbody').append(paymentRow);
                });

                if ($('ul.pagination li').length - 2 !== response.totalPages) {
                    $('ul.pagination').empty();
                    buildSearchPagination(response);
                }
            },
            error: function () {
                window.location = "/";
            }
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
            fetchPayments(0);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "»") {
            fetchPayments(totalPages - 1);
            let currentActive = active;
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "›") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue < totalPages) {
                let currentActive = active;
                startPage = activeValue;
                fetchPayments(startPage);
                active.removeClass("active");
                currentActive.next().addClass("active");
            }
        } else if (val.toUpperCase() === "‹") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue > 1) {
                startPage = activeValue - 2;
                fetchPayments(startPage);
                let currentActive = active;
                currentActive.removeClass("active");
                currentActive.prev().addClass("active");
            }
        } else {
            startPage = parseInt(val - 1);
            fetchPayments(startPage);
            active.removeClass("active");
            $(this).parent().addClass("active");
        }
    });
});