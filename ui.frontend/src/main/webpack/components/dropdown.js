(function() {
    "use strict";

    var selectors = {
        self: '[data-cmp-is="launchdates"]',
        dropdown: '[data-cmp-hook-launchdates="dropdown"]'
    };

    function LaunchDates(config) {

        function init(config) {
            config.element.removeAttribute("data-cmp-is");

            var dropdown = config.element.querySelector(selectors.dropdown);

            if (!dropdown) {
                console.error("Dropdown element not found!");
                return;
            }

            async function getLaunchDates() {
                try {
                    // eslint-disable-next-line max-len
                    const response = await fetch('http://localhost:4502/api/assets/pechakuchas/content-fragments/en.json?limit=30');
                    const data = await response.json();
                    return data.entities;
                } catch (error) {
                    console.error('Error fetching launch dates:', error);
                    dropdown.innerHTML = '<option value="">Failed to load dates</option>';
                }
            }

            function populateDropdown(entities) {
                dropdown.innerHTML = '<option value="">Select a date</option>';

                let i = 0;

                entities.forEach(entity => {
                    const dateLocal = entity.properties.elements.datelocal.value;
                    const formattedDate = new Date(dateLocal).toLocaleString();

                    const option = document.createElement('option');
                    option.value = dateLocal;
                    option.textContent = formattedDate;
                    dropdown.appendChild(option);

                    i++;
                    console.log(i);

                });
            }

            getLaunchDates().then(entities => {
                if (entities) {
                    populateDropdown(entities);
                }
            });
        }

        if (config && config.element) {
            init(config);
        }
    }

    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            new LaunchDates({ element: elements[i] });
        }

        var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
        var body = document.querySelector("body");
        var observer = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                var nodesArray = [].slice.call(mutation.addedNodes);
                if (nodesArray.length > 0) {
                    nodesArray.forEach(function(addedNode) {
                        if (addedNode.querySelectorAll) {
                            var elementsArray = [].slice.call(addedNode.querySelectorAll(selectors.self));
                            elementsArray.forEach(function(element) {
                                new LaunchDates({ element: element });
                            });
                        }
                    });
                }
            });
        });

        observer.observe(body, {
            subtree: true,
            childList: true,
            characterData: true
        });
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }

}());
