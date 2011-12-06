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
    
    describe("the search box", function () {
        it("should have the ability to search for tags, and name", function () {
            var sd = parseSearchData("name1 #tag1 name2 #tag2");
            
            expect(sd).toEqual({
                name: "name1 name2",
                tags: "tag1,tag2"
            });
        });
        
        it("should not populate tags if no hashes are present", function () {
            var sd = parseSearchData("name1 name2");
            
            expect(sd).toEqual({
                name: "name1 name2"
            });
        });
        
        it("should not populate names if no non-hashes are present", function () {
            var sd = parseSearchData("#tag1 #tag2");
            
            expect(sd).toEqual({
                tags: "tag1,tag2"
            });
        });
        
        it("should have an empty name field if input is empty", function () {
            var sd = parseSearchData("");
            
            expect(sd).toEqual({
                name: ""
            });
        });
    });

});