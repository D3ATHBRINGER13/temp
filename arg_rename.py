import os
import re

num = 0
nums = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']

with open('client.txt', 'r') as file:
    client = file.read()

x = re.findall("\.[A-Z].* -> .*:", client)

print(len(x))

for i in x:
    x[num] = x[num][1:]
    x[num] = x[num][:len(x[num]) - 1]
    num += 1


def main(classes):
    number = True
    num2 = 0
    for j in classes:
        if re.search("\$", classes[num2]):
            temp = classes[num2].split(" -> ")
            temp2 = temp[0].split("$")
            try:
                if temp2[2]:
                    num3 = 0
                    for k in s.mp[1]:
                        if temp[1][num3] in nums:
                            num3 += 1
                        else:
                            num3 += 1
                            number = False
                    if not number:
                        temp3 = temp2[2][0]
                        temp3 = temp3.lower()
                        temp2[2] = temp2[2][1:]
                        temp4 = temp3 + temp2[2]
                        temp5 = [temp4, temp[1]]
                        replace(temp4[1], temp4[0], num2)
            except IndexError:
                num3 = 0
                for g in temp[1]:
                    if temp[1][num3] in nums:
                        num3 += 1
                        number = True
                    else:
                        num3 += 1
                        number = False
                if not number:
                    temp3 = temp2[1][0]
                    temp3 = temp3.lower()
                    temp2[1] = temp2[1][1:]
                    temp4 = temp3 + temp2[1]
                    temp5 = [temp4, temp[1]]
                    replace(temp4[1], temp4[0], num2)
        else:
            temp = classes[num2].split(" -> ")
            temp2 = temp[0][0]
            temp2 = temp2.lower()
            temp[0] = temp[0][1:]
            temp3 = temp2 + temp[0]
            temp4 = [temp3, temp[1]]
            replace(temp4[1], temp4[0], num2)
        num2 += 1


def replace(obf, deobf, progress):
    os.system("grep -RIl --exclude=arg_rename.py ' " + obf + " ' | xargs sed -i 's/ " + obf + " / " + deobf + " /g'")
    os.system("grep -RIl --exclude=arg_rename.py ' " + obf + ",' | xargs sed -i 's/ " + obf + ",/ " + deobf + ",/g'")
    os.system("grep -RIl --exclude=arg_rename.py ' " + obf + ".' | xargs sed -i 's/ " + obf + "./ " + deobf + "./g'")
    os.system("grep -RIl --exclude=arg_rename.py ' " + obf + ")' | xargs sed -i 's/ " + obf + ")/ " + deobf + ")/g'")
    os.system("grep -RIl --exclude=arg_rename.py ' " + obf + ";' | xargs sed -i 's/ " + obf + ";/ " + deobf + ";/g'")
    os.system("grep -RIl --exclude=arg_rename.py ')" + obf + ".' | xargs sed -i 's/)" + obf + "./)" + deobf + "./g'")
    os.system("grep -RIl --exclude=arg_rename.py ')" + obf + ")' | xargs sed -i 's/)" + obf + ")/)" + deobf + ")/g'")
    os.system("grep -RIl --exclude=arg_rename.py '(" + obf + ")' | xargs sed -i 's/(" + obf + ")/(" + deobf + ")/g'")
    os.system("grep -RIl --exclude=arg_rename.py '(" + obf + " ' | xargs sed -i 's/(" + obf + " /(" + deobf + " /g'")
    os.system("grep -RIl --exclude=arg_rename.py '(" + obf + ",' | xargs sed -i 's/(" + obf + ",/(" + deobf + ",/g'")
    os.system("grep -RIl --exclude=arg_rename.py '+" + obf + ")' | xargs sed -i 's/+" + obf + ")/+" + deobf + ")/g'")
    os.system("grep -RIl --exclude=arg_rename.py '!" + obf + ".' | xargs sed -i 's/!" + obf + "./!" + deobf + "./g'")
    os.system("grep -RIl --exclude=arg_rename.py '    " + obf + ".' | xargs sed -i 's/    " + obf + "./    " + deobf + "./g'")
    for k in nums:
        os.system("grep -RIl --exclude=arg_rename.py ' " + obf + nums[int(k)] + "' | xargs sed -i 's/ " + obf + nums[int(k)] + "/ " + deobf + nums[int(k)] + "/g'")
        os.system("grep -RIl --exclude=arg_rename.py ' " + obf + nums[int(k)] + "' | xargs sed -i 's/ " + obf + nums[int(k)] + "/ " + deobf + nums[int(k)] + "/g'")
        os.system("grep -RIl --exclude=arg_rename.py ' " + obf + nums[int(k)] + "' | xargs sed -i 's/ " + obf + nums[int(k)] + "/ " + deobf + nums[int(k)] + "/g'")
        os.system("grep -RIl --exclude=arg_rename.py ' " + obf + nums[int(k)] + "' | xargs sed -i 's/ " + obf + nums[int(k)] + "/ " + deobf + nums[int(k)] + "/g'")
        os.system("grep -RIl --exclude=arg_rename.py ' " + obf + nums[int(k)] + "' | xargs sed -i 's/ " + obf + nums[int(k)] + "/ " + deobf + nums[int(k)] + "/g'")
        os.system("grep -RIl --exclude=arg_rename.py ')" + obf + nums[int(k)] + "' | xargs sed -i 's/)" + obf + nums[int(k)] + "/)" + deobf + nums[int(k)] + "/g'")
        os.system("grep -RIl --exclude=arg_rename.py ')" + obf + nums[int(k)] + "' | xargs sed -i 's/)" + obf + nums[int(k)] + "/)" + deobf + nums[int(k)] + "/g'")
        os.system("grep -RIl --exclude=arg_rename.py '(" + obf + nums[int(k)] + "' | xargs sed -i 's/(" + obf + nums[int(k)] + "/(" + deobf + nums[int(k)] + "/g'")
        os.system("grep -RIl --exclude=arg_rename.py '(" + obf + nums[int(k)] + "' | xargs sed -i 's/(" + obf + nums[int(k)] + "/(" + deobf + nums[int(k)] + "/g'")
        os.system("grep -RIl --exclude=arg_rename.py '(" + obf + nums[int(k)] + "' | xargs sed -i 's/(" + obf + nums[int(k)] + "/(" + deobf + nums[int(k)] + "/g'")
        os.system("grep -RIl --exclude=arg_rename.py '+" + obf + nums[int(k)] + "' | xargs sed -i 's/+" + obf + nums[int(k)] + "/+" + deobf + nums[int(k)] + "/g'")
        os.system("grep -RIl --exclude=arg_rename.py '!" + obf + nums[int(k)] + "' | xargs sed -i 's/!" + obf + nums[int(k)] + "/!" + deobf + nums[int(k)] + "/g'")
        os.system("grep -RIl --exclude=arg_rename.py '    " + obf + nums[int(k)] + "' | xargs sed -i 's/    " + obf + nums[int(k)] + "/    " + deobf + nums[int(k)] + "/g'")
    print(progress)


main(x)
