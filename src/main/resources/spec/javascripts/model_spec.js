describe("The countdown model and view", function() {

    describe("has automatic sorting functionality", function () {
        
        it("should be sorted by automatically by event date ascending", function () {
            
            var holder = $("<div><div id=\"t\"></div></div>");
            holder = $(holder).find("#t");
            
            var m = model(holder);
            
            var d1= ["d1", (new Date(2011, 12, 5, 0, 0, 0)).getTime()];
            var d2 =["d2", (new Date(2011, 12, 6, 1, 0, 0)).getTime()];
            var d3 =["d3", (new Date(2011, 12, 7, 1, 0, 0)).getTime()];

            var order = [d2, d2, d3, d1, d3, d1, d3, d3, d2, d2, d1];
            var expected_order = ["d1", "d1", "d1", "d2", "d2", "d2", "d2", "d3", "d3", "d3", "d3"];
            
            _(order).chain().zip(_.range(order.length)).each(function (x) {
                m.putCountdown({ name: x[0][0], eventDate: x[0][1], url: "test" + x[1] });
            });
            
            //expect(holder.find("li").length).toBe(3);

            // check the ordering
            expect(_(holder.find("li > h5")).map(function (x) { return $(x).text(); })).toEqual(expected_order);
            
            
        });
        
    });

});