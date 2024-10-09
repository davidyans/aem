document.addEventListener("DOMContentLoaded", () => {
    const dropdown = document.getElementById('launch-dates');

    // API call to get launch dates
    async function getLaunchDates() {
        try {
            // Replace with your actual API endpoint
            const response = await fetch('https://api.spacexdata.com/v4/launches/upcoming');
            const data = await response.json();

            populateDropdown(data);
        } catch (error) {
            console.error('Error fetching data:', error);
            dropdown.innerHTML = '<option value="">Failed to load dates</option>';
        }
    }

    // Populate dropdown with API data
    function populateDropdown(launches) {
        dropdown.innerHTML = '<option value="">Select a date</option>'; // Clear any existing options

        launches.forEach(launch => {
            const option = document.createElement('option');
            option.value = launch.date_utc;
            option.textContent = new Date(launch.date_utc).toLocaleDateString(); // Convert to readable format
            dropdown.appendChild(option);
        });
    }

    // Call the function to fetch data and populate dropdown
    getLaunchDates();
});