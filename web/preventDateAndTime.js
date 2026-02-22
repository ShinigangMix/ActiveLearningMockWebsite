window.onload = function () {
    var startDateInput = document.getElementById('startDate');
    var endDateInput = document.getElementById('endDate');
    var startTimeInput = document.getElementById('startTime');
    var endTimeInput = document.getElementById('endTime');

    startDateInput.addEventListener('change', function () {
        endDateInput.min = startDateInput.value;
        if (endDateInput.value !== '' && startDateInput.value >= endDateInput.value) {
            alert('End date must be after start date');
            endDateInput.value = '';
        }
    });

    endDateInput.addEventListener('change', function () {
        if (startDateInput.value !== '' && endDateInput.value <= startDateInput.value) {
            alert('End date must be after start date');
            endDateInput.value = '';
        }
    });

    // Event listener to prevent conflicting start and end times
    startTimeInput.addEventListener('change', function () {
        if (endTimeInput.value !== '' && startTimeInput.value >= endTimeInput.value) {
            alert('End time must be after start time');
            startTimeInput.value = '';
        }
    });

    endTimeInput.addEventListener('change', function () {
        if (startTimeInput.value !== '' && endTimeInput.value <= startTimeInput.value) {
            alert('End time must be after start time');
            endTimeInput.value = '';
        }
    });
};