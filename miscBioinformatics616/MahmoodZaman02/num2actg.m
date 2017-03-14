function  str=num2actg(mer)
   
    for i=1:length(mer)
        num=mer(i);
        if(num==1)
            str(i)='A';
            elseif(num==2)
            str(i)='T';
            elseif(num==3)
            str(i)='G';
        else %as only c left
            str(i)='C';
         end
    end
end
