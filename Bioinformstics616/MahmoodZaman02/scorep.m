function[sc cstr]=scorep(profile)
    lengthOfP=size(profile);
    lenCol=lengthOfP(2);
    consNumArr=zeros(1,lenCol);
    sc=0;
    for i=1:lenCol
      colum=profile(:,i);
      colLength=length(colum);
      consNum=mode(colum);
      consNumArr(i)=consNum;
      for j=1:colLength
          sc=sc+ (consNum== colum(j));
      end
    end
    cstr=num2actg(consNumArr);
end