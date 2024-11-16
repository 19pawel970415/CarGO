   function adjustValue(id, step) {
      const input = document.getElementById(id);
      const min = parseInt(input.min);
      const max = parseInt(input.max);
      const value = parseInt(input.value) || min;

      const newValue = value + step;
      if (newValue >= min && newValue <= max) {
         input.value = newValue;
      }
   }